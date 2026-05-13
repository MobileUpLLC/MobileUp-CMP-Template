---
name: network
description: Guidelines for API/DTO boundaries, repository-owned Replica data loading, loaded data observation in components, LCE state, data mutations, cache invalidation, ReplicaAction, ReplicaTag 
---

 Layer Contract

- **Data:** API interfaces, DTOs, Repository, RepositoryImpl, Replica creation, DTO -> domain mapping.
- **Domain:** entities, IDs, queries, and list data models.
- **Presentation:** Components observe Replicas and expose `StateFlow<LoadableState<T>>` or
  `StateFlow<PagedState<T>>`; UI renders those states with LCE widgets.

No direct API calls from Components or UI. No DTOs outside the Data layer.

# Choosing Replica Type

| Need parameters? | Need pagination? | Use |
|---|---|---|
| No | No | `Replica<T>` |
| Yes | No | `KeyedReplica<K, T>` |
| No | Yes | `PagedReplica<T>` |
| Yes | Yes | `KeyedPagedReplica<K, T>` |


# Repository Rules

- Create Replicas in `RepositoryImpl`.
- Expose narrow interfaces from repositories: `Replica<T>`, `KeyedReplica<K, T>`,
  `PagedReplica<T>`, `KeyedPagedReplica<K, T>`. Do not expose `Physical*` replica types outside the implementation.
- Keep local filtering and sorting over loaded data in repository-level Replica transformations, not in Components.
- Let fetcher exceptions propagate. The project `observe` extensions localize errors in the Presentation layer.
- Attach user-data tags, such as `ReplicaTags.UserSpecificData`, to account-specific Replicas.

# Component Rules

- Keep source replica and UI state separate:
    `private val itemReplica = repository.itemReplica`
    `override val itemState = itemReplica.observe(this, errorHandler)`
- Use `withKey(key)` or `withKey(keyFlow)` for keyed Replicas.
- Use `keepPreviousData()` before `withKey(...)` for search, tabs, and filters.
- Use `refresh()` for explicit user refresh.
- Use `combine` when several data sources must become one screen state.

# Mutations and Cache

- Repository methods perform mutation API calls.
- After a successful API call, keep visible data in sync with one of:
  - `mutateData` / `setData` for immediate local changes.
  - `replicaClient.sendAction(...)` with behaviours for multiple replica sync.
  - `invalidate()` when data in a replica should be reloaded.
  - `invalidateByTag(...)`  when data in wide group of replicas should be reloaded.
- Keep `mutateData`, `sendAction`, and invalidation in the Repository layer.
- Components only call Repository methods and update local UI flags.
- Prefer targeted cache updates over broad invalidation when the affected data is known.

# References

Use references only when you need concrete snippets:
- [Basic loading](references/1-basic-loading.md) - domain model, API/DTO boundary, `Replica`, `KeyedReplica`.
- [Paged loading](references/2-paged-loading.md) - `PagedReplica`, `KeyedPagedReplica`, pages.
- [Data observation](references/3-data-observation.md) - loaded data observation, `withKey`, `keepPreviousData`, 
  refresh, load next, `combine`.
- [Cache Invalidation](references/4-cache-invalidation.md) - mutation methods, local cache sync,
  replica actions, behaviours, tags, and targeted invalidation.
- [Local filtering and sorting](references/5-local-filtering-sorting.md) - `associate` and `map` for local data transformations.
