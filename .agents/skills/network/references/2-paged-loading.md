# Paging

Use this pattern when a screen loads a list incrementally.

## Domain Types

```kotlin
@JvmInline
value class ItemId(val value: String)

@JvmInline
value class ItemCategoryId(val value: String)

data class Item(
    val id: ItemId,
    val title: String,
    val isFavorite: Boolean
)

enum class ItemSort { Popular, Newest }

data class ItemQuery(
    val searchText: String?,
    val categoryId: ItemCategoryId?,
    val sort: ItemSort
)

data class ItemListData(
    val items: List<Item>,
    val hasNextPage: Boolean
)
```

## Page Mapping

```kotlin
// data/ItemApi.kt
interface ItemApi {
    @GET("api/v1/items/recent")
    suspend fun getRecentItems(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): ItemListResponse

    @GET("api/v1/items/search")
    suspend fun searchItems(
        @Query("search_text") searchText: String?,
        @Query("category_id") categoryId: String?,
        @Query("sort") sort: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): ItemListResponse
}

// data/dto/ItemListResponse.kt
typealias ItemPage = PageWithTotalAmount<Item>

@Serializable
internal data class ItemListResponse(
    @SerialName("total") val total: Int,
    @SerialName("items") val items: List<ItemResponse>
)

@Serializable
internal data class ItemResponse(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("is_favorite") val isFavorite: Boolean
)

internal fun ItemResponse.toDomain() = Item(
    id = ItemId(id),
    title = title,
    isFavorite = isFavorite
)

internal fun ItemListResponse.toPage(pageSize: Int): ItemPage {
    return ItemPage(
        totalAmount = total,
        items = items.map { it.toDomain() },
        hasNextPage = items.size >= pageSize
    )
}
```

## Repository

```kotlin
interface ItemRepository {
    
    val recentItemsReplica: PagedReplica<ItemListData>

    val itemsByQueryReplica: KeyedPagedReplica<ItemQuery, ItemListData>
}

class ItemRepositoryImpl(
    private val api: ItemApi,
    private val replicaClient: ReplicaClient
) : ItemRepository {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override val recentItemsReplica: PagedReplica<ItemListData> =
        replicaClient.createPagedReplica(
            name = "recentItems",
            settings = PagedReplicaSettings(staleTime = 5.minutes),
            tags = setOf(ReplicaTags.UserSpecificData),
            idExtractor = { item -> item.id },
            fetcher = object : PagedFetcher<Item, ItemPage> {
                override suspend fun fetchFirstPage(): ItemPage {
                    return api.getRecentItems(
                        offset = 0,
                        limit = PAGE_SIZE
                    ).toPage(PAGE_SIZE)
                }

                override suspend fun fetchNextPage(currentData: PagedData<Item, ItemPage>): ItemPage {
                    return api.getRecentItems(
                        offset = currentData.items.size,
                        limit = PAGE_SIZE
                    ).toPage(PAGE_SIZE)
                }
            }
        ).map { data ->
            ItemListData(items = data.items, hasNextPage = data.hasNextPage)
        }

    override val itemsByQueryReplica: KeyedPagedReplica<ItemQuery, ItemListData> =
        replicaClient.createKeyedPagedReplica(
            name = "itemsByQuery",
            settings = KeyedPagedReplicaSettings(maxCount = 10),
            tags = setOf(ReplicaTags.UserSpecificData),
            childName = { query -> "query = $query" },
            childSettings = { PagedReplicaSettings(staleTime = 5.minutes) },
            idExtractor = { item -> item.id },
            fetcher = object : KeyedPagedFetcher<ItemQuery, Item, ItemPage> {
                override suspend fun fetchFirstPage(key: ItemQuery): ItemPage {
                    return api.searchItems(
                        searchText = key.searchText,
                        categoryId = key.categoryId?.value,
                        sort = key.sort.name,
                        offset = 0,
                        limit = PAGE_SIZE
                    ).toPage(PAGE_SIZE)
                }

                override suspend fun fetchNextPage(
                    key: ItemQuery,
                    currentData: PagedData<Item, ItemPage>
                ): ItemPage {
                    return api.searchItems(
                        searchText = key.searchText,
                        categoryId = key.categoryId?.value,
                        sort = key.sort.name,
                        offset = currentData.items.size,
                        limit = PAGE_SIZE
                    ).toPage(PAGE_SIZE)
                }
            }
        ).map { _, data ->
            ItemListData(items = data.items, hasNextPage = data.hasNextPage)
        }
}
```

## Code Style
- DTO classes are `internal` and `@Serializable`.
- DTO fields use explicit `@SerialName`, even when the JSON name currently matches the property.
- DTO -> domain/page mapping is done with `internal` `toDomain()` / `toPage(...)`
  extensions declared next to DTOs.
- `PAGE_SIZE` is declared in `RepositoryImpl` as private constant; DTO page mapping receives `pageSize` as a parameter.
