# Example: Router Component

A router component owns `ChildStack`, opens child screens, and coordinates data flow between them.
Router components must implement `PredictiveBackComponent`.

## Interface

```kotlin
interface ItemsComponent : PredictiveBackComponent {
    
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        data class List(val component: ItemListComponent) : Child
        data class Details(val component: ItemDetailsComponent) : Child
        data class Filter(val component: ItemFilterComponent) : Child
    }
}
```

```kotlin
interface ItemListComponent {
    fun applyFilter(filter: ItemFilter)

    sealed interface Output {
        data class ItemDetailsRequested(val itemId: ItemId) : Output
        data class FilterRequested(val currentFilter: ItemFilter) : Output
    }
}

interface ItemDetailsComponent

interface ItemFilterComponent {
    sealed interface Output {
        data class Applied(val filter: ItemFilter) : Output
    }
}
```

## Real

```kotlin
class RealItemsComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, ItemsComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.List,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override fun onBack() = navigation.pop()

    private fun createChild(
        childConfig: ChildConfig,
        componentContext: ComponentContext
    ): ItemsComponent.Child = when (childConfig) {
        ChildConfig.List -> ItemsComponent.Child.List(
            componentFactory.createItemListComponent(
                componentContext = componentContext,
                onOutput = ::onItemListOutput
            )
        )
        is ChildConfig.Details -> ItemsComponent.Child.Details(
            componentFactory.createItemDetailsComponent(
                componentContext = componentContext,
                itemId = childConfig.itemId
            )
        )
        is ChildConfig.Filter -> ItemsComponent.Child.Filter(
            componentFactory.createItemFilterComponent(
                componentContext = componentContext,
                currentFilter = childConfig.currentFilter,
                onOutput = ::onItemFilterOutput
            )
        )
    }

    private fun onItemListOutput(output: ItemListComponent.Output) {
        when (output) {
            is ItemListComponent.Output.ItemDetailsRequested -> {
                navigation.safePush(ChildConfig.Details(output.itemId))
            }
            is ItemListComponent.Output.FilterRequested -> {
                navigation.safePush(ChildConfig.Filter(output.currentFilter))
            }
        }
    }

    private fun onItemFilterOutput(output: ItemFilterComponent.Output) {
        when (output) {
            is ItemFilterComponent.Output.Applied -> {
                childStack.value
                    .getChild<ItemsComponent.Child.List>()
                    ?.component
                    ?.applyFilter(output.filter)

                navigation.pop()
            }
        }
    }

    @Serializable
    private sealed interface ChildConfig {
        @Serializable
        data object List : ChildConfig

        @Serializable
        data class Details(val itemId: ItemId) : ChildConfig

        @Serializable
        data class Filter(val currentFilter: ItemFilter) : ChildConfig
    }
}
```

`ItemDetailsComponent` has no `Output` here. Back navigation is handled by childStack automatically.

## Fake

```kotlin
class FakeItemsComponent(
    child: ItemsComponent.Child = ItemsComponent.Child.List(FakeItemListComponent())
) : ItemsComponent {
    
    override val childStack = createFakeChildStackStateFlow(child)

    override fun onBack() = Unit
}
```

## UI

```kotlin
@Composable
fun ItemsUi(
    component: ItemsComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.collectAsState()

    Children(
        stack = childStack,
        modifier = modifier,
        animation = component.predictiveBackAnimation()
    ) { child ->
        when (val instance = child.instance) {
            is ItemsComponent.Child.List -> ItemListUi(instance.component)
            is ItemsComponent.Child.Details -> ItemDetailsUi(instance.component)
            is ItemsComponent.Child.Filter -> ItemFilterUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun ItemsUiPreview() {
    CustomTheme {
        ItemsUi(FakeItemsComponent())
    }
}
```

## Code Style

- Declare `childStack` compactly in `RealXxxComponent` and `FakeXxxComponent` implementations, without an explicit type.
- Keep the router interface API ordered as `childStack`, user events when present, `Child`, then `Output` when present.
- Keep implementation members ordered as `navigation`, `childStack`, `createChild`, public event methods, child `Output` handlers, then `ChildConfig`.
- Keep `ChildConfig` as a private implementation detail.
