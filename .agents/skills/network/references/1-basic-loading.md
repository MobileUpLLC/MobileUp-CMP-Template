# Basic Loading

Use this pattern for non-paged requests: a single resource without parameters and details by ID.

## Domain Models

```kotlin
@JvmInline
value class ItemId(val value: String)

@JvmInline
value class ItemCategoryId(val value: String)

data class ItemCategory(
    val id: ItemCategoryId,
    val title: String
)

data class DetailedItem(
    val id: ItemId,
    val title: String,
    val description: String,
    val isFavorite: Boolean
)

data class ItemCatalog(
    val featuredTitle: String,
    val availableCategories: List<ItemCategory>
)
```

## API, DTO, Repository

```kotlin

// data/ItemApi.kt
interface ItemApi {
    @GET("api/v1/items/catalog")
    suspend fun getCatalog(): ItemCatalogResponse

    @GET("api/v1/items/{itemId}")
    suspend fun getItemDetails(@Path("itemId") itemId: String): DetailedItemResponse
}

// data/dto/ItemCatalogResponse.kt
@Serializable
internal data class ItemCatalogResponse(
    @SerialName("featured_title") val featuredTitle: String,
    @SerialName("available_categories") val categories: List<ItemCategoryResponse>
)

@Serializable
internal data class ItemCategoryResponse(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String
)

internal fun ItemCategoryResponse.toDomain() = ItemCategory(
    id = ItemCategoryId(id),
    title = title
)

internal fun ItemCatalogResponse.toDomain() = ItemCatalog(
    featuredTitle = featuredTitle,
    availableCategories = categories.map { it.toDomain() }
)

// data/dto/DetailedItemResponse.kt
@Serializable
internal data class DetailedItemResponse(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("is_favorite") val isFavorite: Boolean
)

internal fun DetailedItemResponse.toDomain() = DetailedItem(
    id = ItemId(id),
    title = title,
    description = description,
    isFavorite = isFavorite
)

// data/ItemRepository.kt
interface ItemRepository {
    val itemCatalogReplica: Replica<ItemCatalog>

    val itemDetailsReplica: KeyedReplica<ItemId, DetailedItem>
}

// data/ItemRepositoryImpl.kt
class ItemRepositoryImpl(
    private val api: ItemApi,
    private val replicaClient: ReplicaClient
) : ItemRepository {

    override val itemCatalogReplica: Replica<ItemCatalog> =
        replicaClient.createReplica(
            name = "itemCatalog",
            settings = ReplicaSettings(staleTime = 10.minutes),
            fetcher = {
                api.getCatalog().toDomain()
            }
        )

    override val itemDetailsReplica: KeyedReplica<ItemId, DetailedItem> =
        replicaClient.createKeyedReplica(
            name = "itemDetails",
            settings = KeyedReplicaSettings(maxCount = 20),
            tags = setOf(ReplicaTags.UserSpecificData),
            childName = { "itemId = ${it.value}" },
            childSettings = { ReplicaSettings(staleTime = 5.minutes) },
            fetcher = { id ->
                api.getItemDetails(id.value).toDomain()
            }
        )
}
```

## Code Style
- API interfaces are Ktorfit contracts: use `@GET`, `@POST`, `@Path`, `@Query`, and `@Body`.
- DTO classes are `internal` and `@Serializable`.
- DTO fields use explicit `@SerialName`, even when the JSON name currently matches the property.
- DTO -> domain mapping is done with `internal` extension functions named `toDomain()`, declared
  next to the DTO they map.
