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

class RealItemListComponent(
    componentContext: ComponentContext,
    componentFactory: ComponentFactory
) : ComponentContext by componentContext, ItemListComponent {

    override val currentCityComponent = componentFactory.createCurrentCityComponent(
        childContext(key = "currentCity")
    )

    override val itemsState = ...
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
