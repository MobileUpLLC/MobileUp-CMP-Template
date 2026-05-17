# Project Context for AI Agent

## Project Overview

**Project Name:** MobileUp CMP Template

Template repository for bootstrapping new Mobile projects at MobileUp.
After cloning, teams customize app identity, remove demo functionality, configure project keys, and evolve the template into a production product.

## Tech Stack

**Platforms:** Android, iOS
**Language:** Kotlin, Swift

**Core libraries:**
- **Compose Multiplatform** - UI
- **Decompose** — component architecture and navigation
- **Replica** — data loading, caching, state management
- **Koin** — dependency injection
- **Ktor** + **Ktorfit** — HTTP client
- **Coil** — image loading
- **Kotlin Coroutines** - async operations
- **Kotlinx DateTime** - date and time
- **Kotlin Serialization** — JSON serialization/parsing
- **Detekt** — static code analysis
- **Module Graph Gradle Plugin** — feature dependency graph validation
- Debug only: **Chucker**, **Hyperion**, **Replica DevTools**

---

## Module Structure

```
core/       — Shared infrastructure: network, theme, error handling, message service, widgets,
              storage, utils
              
features/   — Feature modules, package-by-feature
              features/src/commonMain/kotlin/.../features/{feature_name}/
              
shared/     — It combines all features and provides the `SharedApp` entry point for platform apps.

androidApp/ — Android entry points, icon resources, build configurations, Android-specific integrations.

iosApp      — Xcode project, SwiftUI entry point, assets, iOS-specific integrations.
```

Each feature contains `data`, `domain`, `presentation` layers.

---

## Architecture Overview

### Components (Decompose)

Every screen is a **four-part component**:
- `XxxComponent` — interface (StateFlows + event methods + Output)
- `RealXxxComponent` — implementation (business logic, ComponentContext)
- `FakeXxxComponent` — mock data for previews
- `XxxUi` — pure composable, no business logic

Two types: **Regular** (single screen) and **Router** (manages navigation).

Child -> parent communication via `Output` sealed interface — never direct calls.

> For details: **`decompose-components`** skill

### Data Loading (Replica)

- **Repository** creates Replicas (`createReplica`, `createKeyedReplica`, `createPagedReplica`, `createKeyedPagedReplica`)
- **Component** observes Replicas → `StateFlow<LoadableState<T>>`
- **UI** uses `LceWidget` or `PullRefreshLceWidget` to handle Loading / Content / Error

> For details: **`network`** skill

### Feature Structure

Each feature uses the same base structure: `DI.kt`, `data/`, `domain/`, `presentation/`.
Larger features may organize distinct domains or contexts as subfeature packages.

> For details: **`feature-structure`** skill

### Dependency Injection (Koin)

Each feature has `DI.kt` with a Koin module and `ComponentFactory.createXxxComponent(...)`
extension functions. Components must not use `ComponentFactory` directly. If a component creates
child components, introduce an `XxxChildComponentFactory` and use it from the component.

### String Resources

`StringDesc` is the universal string type — resolved lazily via `.resolve()` in Compose.

> For details: **`string-resources`** skill

---

## Critical Rules

### 1. CustomTheme, NOT MaterialTheme
Compose UI must use `CustomTheme` as the token source.
Prefer existing widgets from `core.widget` over raw Material controls.

### 2. LCE Pattern for all data-loading screens
- `LceWidget` — forms, detail screens where no pull-to-refresh needed
- `PullRefreshLceWidget` — lists, feeds, dashboards, refreshable details

### 3. Previews for UI
- Screen UI must have previews wrapped with `AppTheme`.
- Use `FakeXxxComponent` for screen previews.
- Custom reusable widgets should have previews for meaningful visual states.

### 4. Strict Layer Separation
- **Data** — API, DTOs, Repository (Replica creation, DTO → Entity mapping)
- **Domain** — entities, queries, pure functions (no API, no UI)
- **Presentation** — components, composables (no direct API calls)
- DTOs must not leak outside the `data` layer
- Create replicas in **Repository**, expose narrow interface (`Replica<T>`, not `PhysicalReplica<T>`)
- Mutate (mutateData, invalidate, sendAction) in **Repository**
- **No Interactors/UseCases** — logic lives in Repository + Component

### 5. Component Creation
- Real components are created only through `ComponentFactory` extension functions.
- Do not instantiate `RealXxxComponent` directly from parent components or UI.
- Do not pass `ComponentFactory` into `RealXxxComponent`.
- Use `XxxChildComponentFactory` when a component needs to create embedded, dialog, or navigation children.

---

## Agent Working Rules

- Read existing code and state assumptions before changing anything.
- Ask when intent, scope, or tradeoffs are unclear.
- Prefer the simplest solution that fully satisfies the request.
- Do not add speculative features, abstractions, or configurability.
- Keep edits surgical and directly tied to the requested behavior.
- Match local style, architecture, naming, and module boundaries.
- Do not refactor, reformat, or clean up unrelated code.
- Remove only unused code introduced by your own changes.
- Define verifiable success criteria before implementation.
- Validate with focused tests or checks, and report remaining risks.
---

## Testing Rules

- Tests live under `features/src/commonTest`.
- Use Kotest `FunSpec` as the default test style.
- Use the existing `componentTest` DSL for Decompose component tests, including screen tests that cover
  `Component -> Repository -> Network -> State / Output` and router/root tests with test child factories.
- Name tests in English by observable behavior, not implementation details.

---

## Git Workflow

**Branch naming:**
- `feature/<JIRA_KEY>-XXXX/description`
- `bugfix/<JIRA_KEY>-XXXX/description`

**Flow:** branch from `develop` → MR to `develop` → code review → merge
**Production:** `master` (merged from `develop`)

---

## Build Variants

**Flavors:** `dev` (development backend) / `prod` (production backend)
**Types:** `debug` (with dev tools) / `release` (ProGuard)

Concrete Android variants:
- `devDebug` — default local validation target
- `prodDebug` — validates production backend flavor wiring
- `prodRelease` — validate shrinker, resources, signing, and ProGuard/R8 behavior

---

## Validation Workflow

For changes in common/Android code use only this validation command:

```bash
./gradlew detekt :features:testAndroidHostTest :androidApp:assembleDevDebug
```

For iOS-specific or KMP shared API changes, also run:

```bash
./gradlew :features:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosSimulatorArm64
```
