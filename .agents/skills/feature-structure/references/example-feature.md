# Example Feature

A concrete example of how a feature may look.

## File Tree

```text
catalog/
├── DI.kt
├── data/
│   ├── CatalogApi.kt
│   ├── CatalogRepository.kt
│   ├── CatalogRepositoryImpl.kt
│   └── dto/
│       ├── CatalogItemResponse.kt
│       └── CatalogFilterResponse.kt
├── domain/
│   ├── CatalogItem.kt
│   ├── CatalogItemId.kt
│   ├── CatalogFilter.kt
│   └── CatalogQuery.kt
└── presentation/
    ├── CatalogComponent.kt
    ├── RealCatalogComponent.kt
    ├── FakeCatalogComponent.kt
    ├── CatalogUi.kt
    ├── list/
    │   ├── CatalogListComponent.kt
    │   ├── RealCatalogListComponent.kt
    │   ├── FakeCatalogListComponent.kt
    │   └── CatalogListUi.kt
    ├── details/
    │   ├── CatalogDetailsComponent.kt
    │   ├── RealCatalogDetailsComponent.kt
    │   ├── FakeCatalogDetailsComponent.kt
    │   └── CatalogDetailsUi.kt
    └── widget/
        └── CatalogCard.kt
```

## `catalog_strings.xml`

Path:

`features/src/commonMain/composeResources/values/catalog_strings.xml`

```xml
<resources>
    <string name="catalog_title">Catalog</string>
    <string name="catalog_list_search_placeholder">Search</string>
    <string name="catalog_list_empty">Nothing found</string>
    <string name="catalog_list_retry">Retry</string>
    <string name="catalog_details_title">Item details</string>
    <string name="catalog_details_add_to_favorites">Add to favorites</string>
    <string name="catalog_filters_title">Filters</string>
    <string name="catalog_filters_apply">Apply</string>
</resources>
```

## `DI.kt`

```kotlin
val catalogModule = module {
    single<CatalogApi> { get<NetworkApiFactory>().unauthorizedKtorfit.createCatalogApi() }
    single<CatalogRepository> { CatalogRepositoryImpl(get(), get()) }
}

fun ComponentFactory.createCatalogComponent(
    componentContext: ComponentContext
): CatalogComponent {
    return RealCatalogComponent(componentContext, this)
}

fun ComponentFactory.createCatalogListComponent(
    componentContext: ComponentContext,
    onOutput: (CatalogListComponent.Output) -> Unit
): CatalogListComponent {
    return RealCatalogListComponent(componentContext, onOutput, get(), get())
}

fun ComponentFactory.createCatalogDetailsComponent(
    componentContext: ComponentContext,
    itemId: CatalogItemId
): CatalogDetailsComponent {
    return RealCatalogDetailsComponent(componentContext, itemId, get(), get())
}
```
