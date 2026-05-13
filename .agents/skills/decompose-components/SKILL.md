---
name: decompose-components
description: Rules for structuring and connecting Decompose components - regular components, routers, Outputs, factories, navigation, and embedded children
---

## Core Structure

Every component uses four files:

- `XxxComponent` — public contract: state flows, user actions, optional `Output`.
- `RealXxxComponent` — implementation: `ComponentContext by componentContext`, business logic,
  dependencies.
- `FakeXxxComponent` — preview/testing stub with static data and no-op actions.
- `XxxUi` — pure UI that reads component state and calls component methods.

Keep the public API on the interface. Keep business logic in `RealXxxComponent`. Keep `XxxUi` free
of business logic.

## Component Types

### Regular Component

- Does not own `ChildStack`.
- May be a standalone screen.
- May be an embedded child inside another component.
- Add `Output` only when the parent must react to something.

### Router Component

- Owns `ChildStack` and `StackNavigation`.
- MUST implement `PredictiveBackComponent`.
- Use for feature entry points and multi-screen flows.

## Core Rules

### Output

- Never call parent methods directly. Upward communication goes through `Output`.
- Use `Output` only when the component must communicate upward.
- For output names prefer completed domain events that let the parent choose the reaction: `ItemSaved`, `LoggedOut`.
- Use `<Something>Requested` only when the child action has one clear meaning and a completed event would feel
  artificial: `ItemDetailsRequested`, `FilterRequested`.

### Factories

- Create real components through `ComponentFactory.createXxxComponent(...)` extension functions.
- Parents call factory methods; do not instantiate `RealXxxComponent` directly.
- Keep dependency wiring in DI/factory code, not in parent components.

### Navigation

- Prefer `safePush()` over `push()`.
- Use `pop()`, `replaceCurrent()`, and `replaceAll()` for standard router transitions.
- Use `getChild()` only to pass data back to an existing requester in the stack.

### Embedded Children

- A permanent embedded child is created with `childContext(key = "...")`.
- Every child gets its own `ComponentContext`.
- Never pass the same parent `ComponentContext` directly to multiple children.

### Component API Shape

- Expose clearly named state like `itemsState`, `profileState`, `detailsState`; never use generic
  `state`.
- Expose user actions as explicit methods on the component interface.
- If state belongs to the component and is mutated directly by its actions, use `MutableStateFlow`.

### Dialogs and Messages

- Components may own dialog controls and may call `MessageService`.

## References

Use only when you need specific details:

- [Regular Component](references/1-regular-component.md) — minimal standalone regular component
  pattern.
- [Router Component](references/2-router-component.md) — router structure, navigation, `getChild()`.
- [Adding Screen to Router](references/3-add-screen-to-router.md) — checklist for updating an
  existing router.
- [Embedded Child Component](references/4-embedded-child-component.md) — permanent child via
  `childContext(key)`.
- [Dialogs](references/5-dialogs.md) — dialog control patterns.
- [Messages](references/6-messages.md) — `MessageService` usage.
