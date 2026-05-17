# Example: Embedded Child Component

An embedded child component is a regular component that is created and rendered inside another
component. The child shape stays the same; only the parent creation pattern changes.

## When to Use

- The child is always present on the screen.
- The child is reusable across multiple screens.
- You do not need navigation state for this child.

## Parent

```kotlin
interface ItemListComponent {

    val currentCityComponent: CurrentCityComponent

    val itemsState: StateFlow<LoadableState<List<Item>>>
}

interface ItemListChildComponentFactory {
    fun createCurrentCityComponent(
        componentContext: ComponentContext
    ): CurrentCityComponent
}

class RealItemListChildComponentFactory(
    private val componentFactory: ComponentFactory
) : ItemListChildComponentFactory {
    override fun createCurrentCityComponent(
        componentContext: ComponentContext
    ) = componentFactory.createCurrentCityComponent(componentContext)
}

class RealItemListComponent(
    componentContext: ComponentContext,
    childComponentFactory: ItemListChildComponentFactory
) : ComponentContext by componentContext, ItemListComponent {

    override val currentCityComponent = childComponentFactory.createCurrentCityComponent(
        childContext(key = "currentCity")
    )

    override val itemsState = ...
}

fun ComponentFactory.createItemListComponent(
    componentContext: ComponentContext
): ItemListComponent {
    return RealItemListComponent(
        componentContext = componentContext,
        childComponentFactory = RealItemListChildComponentFactory(this)
    )
}
```

## UI

```kotlin
@Composable
fun ItemListUi(component: ItemListComponent) {
    val itemsState by component.itemsState.collectAsState()

    Column {
        CurrentCityUi(component.currentCityComponent)
        
        // List content driven by itemsState
    }
}
```

## Watchouts

Use `childContext(key = "...")` for permanent embedded children. Do not pass the same parent
`ComponentContext` directly to multiple children.

If a component creates embedded children, depend on an `XxxChildComponentFactory` from the
component. Do not pass `ComponentFactory` into `RealXxxComponent`.
