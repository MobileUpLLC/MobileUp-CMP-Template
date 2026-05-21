# Example: Regular Component

A regular component does not own `ChildStack`: it exposes state, user actions, and optional `Output`.

## Interface

```kotlin
interface ItemListComponent {

    val itemsState: StateFlow<LoadableState<List<Item>>>

    fun onRefresh()

    fun onItemClick(itemId: ItemId)

    sealed interface Output {
        data class ItemDetailsRequested(val itemId: ItemId) : Output
    }
}
```

## Real

```kotlin
class RealItemListComponent(
    componentContext: ComponentContext,
    private val onOutput: (ItemListComponent.Output) -> Unit,
    errorHandler: ErrorHandler,
    repository: ItemRepository
) : ComponentContext by componentContext, ItemListComponent {

    private val itemsReplica = repository.itemsReplica

    override val itemsState = itemsReplica.observe(this, errorHandler)

    override fun onRefresh() {
        itemsReplica.refresh()
    }

    override fun onItemClick(itemId: ItemId) {
        onOutput(ItemListComponent.Output.ItemDetailsRequested(itemId))
    }
}
```

## Fake

Fake data is declared on the corresponding domain model or UI-state class, not inside
`FakeXxxComponent`:

```kotlin
data class Item(
    val id: ItemId,
    val name: String
) {
    companion object {
        val FAKE_LIST = listOf(
            Item(id = ItemId("1"), name = "First item"),
            Item(id = ItemId("2"), name = "Second item")
        )
    }
}
```

```kotlin
class FakeItemListComponent(
    private val onOutput: (ItemListComponent.Output) -> Unit = {}
) : ItemListComponent {
    override val itemsState = MutableStateFlow(LoadableState(data = Item.FAKE_LIST))

    fun emitOutput(output: ItemListComponent.Output) {
        onOutput(output)
    }

    override fun onRefresh() = Unit

    override fun onItemClick(itemId: ItemId) = Unit
}
```

Fake components must not implement user action behavior. Every user action returns `Unit`; only
`emitOutput(output)` may call the output callback for tests.

## UI

```kotlin
@Composable
fun ItemListUi(
    component: ItemListComponent,
    modifier: Modifier = Modifier
) {
    val itemsState by component.itemsState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ... }
    ) { innerPadding ->
        PullRefreshLceWidget(
            state = itemsState,
            innerPadding = innerPadding,
            onRefresh = component::onRefresh,
        ) { items, contentPadding ->
            // Content
        }
    }
}

@Preview
@Composable
private fun ItemListUiPreview() {
    CustomTheme {
        ItemListUi(FakeItemListComponent())
    }
}
```

## Code Style

- Declare `RealXxxComponent` constructor parameters in this order: `componentContext`, `onOutput`
  callback, screen arguments, factories/services/repositories/handlers.
- Use `private val` for constructor parameters only when the dependency must be stored as a class
  field; otherwise keep it as a plain parameter.
- For local mutable state, expose `StateFlow` in the interface and override it with
  `MutableStateFlow` in the implementation; do not create `_someState`/`someState` pairs.
- Keep the public interface API ordered as embedded child components, state properties, user events, then `Output`.
- Name component methods as user events with `onSomething`, not commands like `doSomething`.
- Make `Output` events meaningful to the parent instead of exposing internal implementation details.
- Fake components with `Output` accept an output callback with a no-op default and expose
  `emitOutput(output)` for tests.
- Put fake data in `companion object` of the corresponding domain or UI-state class, not in
  `FakeXxxComponent`.