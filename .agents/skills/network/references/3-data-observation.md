# Observation

Components observe Replicas and expose LCE state to UI.
UI reads `xxxState` and render content/loading/error with `LceWidget` or `PullRefreshLceWidget`.
Component interfaces expose only state and actions; Replica objects stay private.

## Replica

```kotlin
class RealItemCatalogComponent(
    componentContext: ComponentContext,
    itemRepository: ItemRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, ItemCatalogComponent {

    private val itemCatalogReplica = itemRepository.itemCatalogReplica

    override val itemCatalogState = itemCatalogReplica.observe(this, errorHandler)

    override fun onRefresh() {
        itemCatalogReplica.refresh()
    }
}
```

## KeyedReplica

```kotlin
class RealItemDetailsComponent(
    componentContext: ComponentContext,
    itemId: ItemId,
    itemRepository: ItemRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, ItemDetailsComponent {

    private val itemReplica = itemRepository.itemDetailsReplica.withKey(itemId)

    override val itemState = itemReplica.observe(this, errorHandler)

    override fun onRefresh() {
        itemReplica.refresh()
    }
}
```

## PagedReplica

```kotlin
class RealRecentItemsComponent(
    componentContext: ComponentContext,
    itemRepository: ItemRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, RecentItemsComponent {

    private val recentItemsReplica = itemRepository.recentItemsReplica

    override val recentItemsState = recentItemsReplica.observe(this, errorHandler)

    override fun onRefresh() {
        recentItemsReplica.refresh()
    }

    override fun onLoadNext() {
        recentItemsReplica.loadNext()
    }
}
```

Use `TriggerLoadNext` inside the content branch, where paged state and loaded data are available:

```kotlin
lazyListState.TriggerLoadNext(
    pagedState = recentItemsState,
    hasNextPage = recentItems.hasNextPage,
    callback = component::onLoadNext
)
```

## KeyedPagedReplica

Use `keepPreviousData()` for query-driven lists to keep old content visible while a new key loads.

```kotlin
class RealFilteredItemsComponent(
    componentContext: ComponentContext,
    itemRepository: ItemRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, FilteredItemsComponent {

    private val itemQuery = MutableStateFlow(
        ItemQuery(
            searchText = null,
            categoryId = null,
            sort = ItemSort.Popular
        )
    )

    private val itemsReplica = itemRepository.itemsByQueryReplica
        .keepPreviousData()
        .withKey(itemQuery)

    override val itemsState = itemsReplica.observe(this, errorHandler)

    override fun onRefresh() {
        itemsReplica.refresh()
    }

    override fun onLoadNext() {
        itemsReplica.loadNext()
    }
}
```

## Combine normal replicas

```kotlin
data class ItemHeaderData(
    val catalog: ItemCatalog,
    val item: DetailedItem
)

class RealItemHeaderComponent(
    componentContext: ComponentContext,
    itemId: ItemId,
    itemRepository: ItemRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, ItemHeaderComponent {

    private val itemCatalogReplica = itemRepository.itemCatalogReplica
    private val itemReplica = itemRepository.itemDetailsReplica.withKey(itemId)

    private val headerReplica = combine(
        itemCatalogReplica,
        itemReplica
    ) { catalog, item ->
        ItemHeaderData(
            catalog = catalog,
            item = item
        )
    }

    override val headerState = headerReplica.observe(this, errorHandler)

    override fun onRefresh() {
        headerReplica.refresh()
    }
}
```

## Combine normal replicas with paged replica

Keep the original paged Replica separately when combined data still needs next-page loading.
Expose separate paged state for `TriggerLoadNext`, and call `loadNext()` on the original paged Replica.

```kotlin
data class ItemOverviewData(
    val catalog: ItemCatalog,
    val recentItems: ItemListData
)

class RealItemOverviewComponent(
    componentContext: ComponentContext,
    itemRepository: ItemRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, ItemOverviewComponent {

    private val itemCatalogReplica = itemRepository.itemCatalogReplica
    private val recentItemsReplica = itemRepository.recentItemsReplica

    private val overviewReplica = combine(
        itemCatalogReplica,
        recentItemsReplica.toReplica()
    ) { catalog, recentItems ->
        ItemOverviewData(
            catalog = catalog,
            recentItems = recentItems
        )
    }

    override val overviewState = overviewReplica.observe(this, errorHandler)
    override val pagedState = recentItemsReplica.observe(this, errorHandler) // StateFlow<PagedState<*>>

    override fun onRefresh() {
        overviewReplica.refresh()
    }

    override fun onLoadNext() {
        recentItemsReplica.loadNext()
    }
}
```

Use the original paged state for the next-page trigger:

```kotlin
lazyListState.TriggerLoadNext(
    pagedState = pagedState,
    hasNextPage = overview.recentItems.hasNextPage,
    callback = component::onLoadNext
)
```

## Code Style

- Declare every observed Replica as a separate `private val xxxReplica` field.
- Name observed UI state with the same domain prefix and `State` suffix: `itemReplica` -> `itemState`.
- Keep the public state declaration directly after its source Replica:
  `override val xxxState = xxxReplica.observe(this, errorHandler)`.
- Bind keyed Replicas in the source field with `.withKey(keyValue)` or `.withKey(keyFlow)`, not inside `observe(...)`.
- For query-driven lists, put `.keepPreviousData()` before `.withKey(keyFlow)`.
- Name the refresh event `onRefresh()` and call `xxxReplica.refresh()` inside it.
- Name the next-page event `onLoadNext()` and call `xxxReplica.loadNext()` inside it.
- For combined state, declare source Replicas first, then `private val xxxReplica = combine(...)`.
- When a combined state includes paged data, expose a separate `pagedState` observed from the
  original paged Replica and call `loadNext()` on that original Replica.
