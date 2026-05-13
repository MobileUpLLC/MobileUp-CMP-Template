# Local Filtering And Sorting

Local filtering and sorting over loaded data belong in the Repository layer, even when they do not require a new network request.
Components should only change the query/key and observe an already prepared Replica.


```kotlin
// data/ItemRepositoryImpl.kt
private val allItemsReplica: Replica<List<Item>> =
    replicaClient.createReplica(...)

override val itemsByQueryReplica: KeyedReplica<ItemQuery, List<Item>> =
   associate { query: ItemQuery ->
        allItemsReplica.map { items ->
            items
                .filter(query.filter)
                .sorted(query.sorting)
        }
    }
```

`allItemsReplica` loads the source list. The public keyed Replica applies local query-dependent transformations in the data layer.
