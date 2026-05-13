---
name: feature-structure
description: Guidelines for organizing feature module structure using package-by-feature principle
---

# Feature Structure

This skill defines the structure of a feature module in this project.

The main idea is simple:

- every feature uses the same internal structure
- a subfeature is the same idea applied deeper inside a larger feature
- subfeatures help organize larger features around distinct contexts

## Core Principle

A feature is a self-contained package with four main parts:

- `DI.kt`
- `data/`
- `domain/`
- `presentation/`

This structure stays the same even when the feature grows.

If a feature later contains clearly separable domains or contexts, you may introduce subfeatures.
Each subfeature follows the same `data / domain / presentation` logic inside its own scope.

## Canonical Feature Structure

Use this as the default shape:

Names in angle brackets are placeholders and should be replaced with project-specific names.

```text
<feature_name>/
├── DI.kt
├── data/
│   ├── <Feature>Api.kt
│   ├── <Feature>Repository.kt
│   ├── <Feature>RepositoryImpl.kt
│   └── dto/
│       ├── <Entity>Response.kt
│       ├── <AnotherEntity>Response.kt
│       └── <Action>Request.kt    # Optional
├── domain/
│   ├── <Entity>.kt
│   ├── <Entity>Id.kt
│   ├── <AnotherEntity>.kt
│   ├── <OneMoreEntity>.kt
│   └── <Entity>Query.kt                # Optional
└── presentation/
    ├── <Feature>Component.kt
    ├── Real<Feature>Component.kt
    ├── Fake<Feature>Component.kt
    ├── <Feature>Ui.kt
    ├── <screen_name>/            # Optional screen package
    ├── <another_screen_name>/    # Optional screen package
    └── widget/                   # Optional feature-specific widgets
```

Nested screen packages inside `presentation/` are a regular way to organize screens within one
feature.

## Optional Extension: Subfeatures

If a feature contains logically separable domains or contexts, the package tree may expand like this:

```text
feature_name/
├── DI.kt
├── data/
│   ├── subfeature_a/
│   ├── subfeature_b/
│   └── common/                 # Rare; only when truly justified
├── domain/
│   ├── subfeature_a/
│   ├── subfeature_b/
│   └── common/
└── presentation/
    ├── subfeature_a/
    ├── subfeature_b/
    └── common/
```

Subfeatures repeat the same internal structure within a narrower context.

Subfeatures make sense when:

- the feature contains distinct business contexts or workflows
- each context wants its own data, domain, and presentation organization
- the split improves clarity more than it increases navigation overhead

If the feature is still one cohesive domain, keep a single feature package and organize only the
needed screens under `presentation/`.

## Placement Rules

### `data/`

Put infrastructure-facing code here:

- API interfaces
- repository contract and implementation
- DTOs and mapping helpers
- Replica creation and mutation logic inside repository implementations

### `domain/`

Put product-facing models here:

- entities
- ids
- query objects
- pure domain calculations if they belong to this feature

Do not place API DTOs or UI-specific formatting here.

### `presentation/`

Put screen-facing code here:

- components
- composable functions
- feature-specific widgets
- optional view data only when serious UI transformation is needed

Do not call APIs directly from presentation.

### `DI.kt`

Place `DI.kt` at the root of the feature package.

It contains:

- the Koin module for the feature
- `ComponentFactory.createXxxComponent(...)` extension functions

Add one factory function for each public real component created through `DI.kt`.

## Naming

Use one clear naming story across the whole feature.

Examples:
- Feature package: `catalog`, `profile`, `settings`
- Feature root component: `CatalogComponent`, `RealCatalogComponent`, `FakeCatalogComponent`, `CatalogUi`
- Nested screen component: `CatalogListComponent`, `CatalogDetailsComponent`
- Domain entity: `CatalogItem`, `CatalogFilter`, `CatalogItemId`
- API and repository: `CatalogApi`, `CatalogRepository`, `CatalogRepositoryImpl`
- Query object: `CatalogQuery`
- View data: `CatalogItemViewData` only when presentation needs a meaningful transformation
- Widget: describe the actual UI piece, for example `CatalogCard`

## String Resources

Location:

`features/src/commonMain/composeResources/values/{feature_name}_strings.xml`

Naming pattern:

- `{feature}_{context}_{element}`
- add a subfeature prefix only when strings are specific to that subfeature

Examples:

- `catalog_title`
- `catalog_list_empty`
- `catalog_details_title`
- `catalog_filters_apply`

Use the feature name as the stable prefix for the whole resource file.

## Anti-Patterns

- Don’t duplicate app-wide widgets inside a feature; put reusable global widgets in `/core/widget/`.
- Don’t move business logic into `Ui` files.
- Don’t instantiate real components directly; create them through `ComponentFactory`.

## Reference

Use this only when you need a concrete illustration:

- [Example Feature](references/example-feature.md) - realistic `catalog` example with structure,
  strings, and `DI.kt`
