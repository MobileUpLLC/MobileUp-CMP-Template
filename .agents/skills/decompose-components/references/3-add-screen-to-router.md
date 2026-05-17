# Example: Adding New Screen to Router

Practical checklist for updating an existing router when you add a new screen.

For the full router pattern, see [Router Component](2-router-component.md).
For the four-part structure of the new screen itself, see [Regular Component](1-regular-component.md).

## Scenario

We have an `ItemsComponent` router with list and details screens. We want to add `ItemFilterComponent`.

## Checklist

1. Create the new screen using the standard four-part structure:

```text
presentation/filter/
  ItemFilterComponent.kt
  RealItemFilterComponent.kt
  FakeItemFilterComponent.kt
  ItemFilterUi.kt
```

2. Add `ComponentFactory.createItemFilterComponent(...)` in `DI.kt`.

3. Add a new `ChildConfig` entry for the screen:

```kotlin
@Serializable
data class Filter(val currentFilter: ItemFilter) : ChildConfig
```

4. Add a new `Child` entry:

```kotlin
data class Filter(val component: ItemFilterComponent) : Child
```

5. Add the new screen to `XxxChildComponentFactory` / `RealXxxChildComponentFactory`, then handle the new config in `createChild(...)`:

```kotlin
is ChildConfig.Filter -> ItemsComponent.Child.Filter(
    childComponentFactory.createItemFilterComponent(
        componentContext = componentContext,
        currentFilter = childConfig.currentFilter,
        onOutput = ::onItemFilterOutput
    )
)
```

6. If the new screen communicates with the parent, add the output handler:

```kotlin
private fun onItemFilterOutput(output: ItemFilterComponent.Output) {
    when (output) {
        is ItemFilterComponent.Output.Applied -> {
            childStack.value
                .getChild<ItemsComponent.Child.List>()
                ?.component
                ?.applyFilter(output.filter)

            navigation.pop()
        }
    }
}
```

7. Add a navigation trigger from the existing child that opens the new screen:

```kotlin
is ItemListComponent.Output.FilterRequested -> {
    navigation.safePush(ChildConfig.Filter(output.currentFilter))
}
```

8. Render the new child in `Children(...)`:

```kotlin
is ItemsComponent.Child.Filter -> ItemFilterUi(instance.component)
```

## Final Check

- [ ] The new screen has all 4 files.
- [ ] `ComponentFactory` can create the screen.
- [ ] `XxxChildComponentFactory` and `RealXxxChildComponentFactory` can create the component.
- [ ] `ChildConfig` and `Child` both include the new screen.
- [ ] `createChild(...)` handles the new config.
- [ ] Router output handling is updated if the screen emits `Output`.
- [ ] An existing screen can actually open the new screen.
- [ ] `Children(...)` renders the new child.

## Watchouts

- `ChildConfig` should contain only data that can be serialized and restored safely.
- Use `safePush()` instead of `push()` to prevent crash when same ChildConfig added twice.
