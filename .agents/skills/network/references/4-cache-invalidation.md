# Cache Invalidation

After a successful mutating API request, synchronize already loaded Replica data so the UI does not show stale state until the next reload.

Keep this synchronization in the Repository layer. Components should only call Repository mutation methods and update local UI flags.

## Invalidate Concrete Replicas

Use targeted invalidation when you know which Replica is affected, but the safe local transformation is unknown, incomplete, or too expensive to maintain.

```kotlin
class ItemRepositoryImpl(
    private val api: ItemApi,
    private val replicaClient: ReplicaClient
) : ItemRepository {

    override suspend fun deleteItem(itemId: ItemId) {
        api.deleteItem(itemId.value)

        itemDetailsReplica.clear(itemId)
        itemCatalogReplica.invalidateAll()
    }
}
```

Use this for destructive mutations, complex list membership rules, server-calculated fields, or any case where refetching is clearer than rebuilding local state.

## Mutate Concrete Replicas

Use `mutateData` when the exact local change is known and can be safely applied to one loaded Replica.

```kotlin
class ItemRepositoryImpl(
    private val api: ItemApi
) : ItemRepository {

    override suspend fun setTitle(itemId: ItemId, title: String) {
        api.setTitle(itemId.value, title)

        itemDetailsReplica.mutateData(itemId) { item ->
            item.copy(title = title)
        }
    }
}
```

Prefer this for simple field updates on a known entity, especially when refetching would be slower and the server response does not add new information.

## Invalidate Many Replicas By Tag

Use tags when one event affects a whole group of Replicas, for example logout, account switch, or a full user-data refresh.

```kotlin
suspend fun logout() {
    replicaClient.clearByTag(
        tag = ReplicaTags.UserSpecificData,
        invalidationMode = InvalidationMode.RefreshIfHasObservers
    )
}

suspend fun refreshAllUserData() {
    replicaClient.invalidateByTag(ReplicaTags.UserSpecificData)
}
```

Attach tags when creating user-specific Replicas:

```kotlin
override val itemDetailsReplica: KeyedReplica<ItemId, DetailedItem> =
    replicaClient.createKeyedReplica(
        name = "itemDetails",
        tags = setOf(ReplicaTags.UserSpecificData),
        // ...
    )
```

Use `clearByTag` when cached data must disappear immediately.

## Invalidate Or Mutate Through Actions

Use actions when one mutation must update several Replicas that may contain the same item.

```kotlin
data class SetItemFavoriteAction(
    val itemId: ItemId,
    val isFavorite: Boolean
) : ReplicaAction

private fun Item.withFavoriteFrom(action: SetItemFavoriteAction): Item {
    return if (id == action.itemId) {
        copy(isFavorite = action.isFavorite)
    } else {
        this
    }
}
```

Send the action from the Repository mutation method after the API request succeeds:

```kotlin
override suspend fun setFavorite(itemId: ItemId, isFavorite: Boolean) {
    api.setFavorite(itemId.value, isFavorite)

    replicaClient.sendAction(
        SetItemFavoriteAction(itemId, isFavorite)
    )
}
```

Handle the action in every affected Replica:

```kotlin
override val itemsByQueryReplica: KeyedPagedReplica<ItemQuery, ItemListData> =
    replicaClient.createKeyedPagedReplica(
        name = "itemsByQuery",
        // ...
        childBehaviours = {
            listOf(
                ReplicaBehaviour.mutateOnAction { action: SetItemFavoriteAction, data: ItemListData ->
                    data.copy(
                        items = data.items.map { item ->
                            item.withFavoriteFrom(action)
                        }
                    )
                }
            )
        }
    )

override val favoriteItemsReplica: Replica<List<Item>> =
    replicaClient.createReplica(
        name = "favoriteItems",
        // ...
        behaviours = listOf(
            ReplicaBehaviour.doOnAction { action: SetItemFavoriteAction ->
                if (action.isFavorite) {
                    invalidate()
                } else {
                    mutateData { items ->
                        items.filter { it.id != action.itemId }
                    }
                }
            }
        )
    )
```

`mutateOnAction` is good when the existing data contains enough information to update each item. `doOnAction` is good when the Replica must choose between mutation and invalidation; in the example above, removing from favorites is local, but adding invalidates because the favorites list may not have enough data to insert the full item correctly.

## Optimistic Updates

Use optimistic updates when the UI should react immediately, before the network request completes. Replica applies the action first and rolls it back if the request fails.

```kotlin
override suspend fun setFavorite(itemId: ItemId, isFavorite: Boolean) {
    replicaClient.sendOptimisticAction(
        action = SetItemFavoriteAction(itemId, isFavorite)
    ) {
        api.setFavorite(itemId.value, isFavorite)
    }
}
```

The affected Replicas use the same action behaviours as non-optimistic updates:

```kotlin
override val allItemsReplica: Replica<List<Item>> =
    replicaClient.createReplica(
        name = "allItems",
        // ...
        behaviours = listOf(
            ReplicaBehaviour.mutateOnAction { action: SetItemFavoriteAction, items ->
                items.map { item ->
                    item.withFavoriteFrom(action)
                }
            }
        )
    )

override val favoriteItemsReplica: Replica<List<Item>> =
    replicaClient.createReplica(
        name = "favoriteItems",
        // ...
        behaviours = listOf(
            ReplicaBehaviour.mutateOnAction { action: SetItemFavoriteAction, items ->
                items.map { item ->
                    item.withFavoriteFrom(action)
                }
            },
            ReplicaBehaviour.doOnAction { action: SetItemFavoriteAction ->
                if (action.isFavorite) {
                    invalidate()
                } else {
                    mutateData { items ->
                        items.filter { it.id != action.itemId }
                    }
                }
            }
        )
    )
```

Use optimistic updates only when rollback is acceptable and the action fully describes the temporary UI change.
For risky mutations, call the API first and then use `sendAction`, `mutateData`, or invalidation after success.

## Key Points

- Keep `mutateData`, `sendAction`, `sendOptimisticAction`, and invalidation inside Repository methods.
- Prefer `mutateData` when the exact local change is known.
- Prefer concrete `invalidate` calls when local transformation is risky.
- Prefer `invalidateByTag` or `clearByTag` for account-wide or session-wide cache changes.
- Prefer actions when the same mutation must synchronize several Replicas.
