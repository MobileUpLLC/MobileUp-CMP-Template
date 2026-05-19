---
name: compose-ui
description: Compose Multiplatform UI patterns for this project - Scaffold screens, AppToolbar, decomposition, CustomTheme, LCE widgets, insets, system bars, messages, core widgets, and previews.
---

# Compose UI

Use this guide when writing screens or reusable Compose widgets in this project.

## Screen Shape

Feature screens use one root composable with `Scaffold`, then private content/widgets.

```kotlin
@Composable
fun XxxUi(component: XxxComponent, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            XxxToolbar(component)
        }
    ) { innerPadding ->
        XxxContent(component, innerPadding)
    }

    StandardDialog(component.dialogControl)
    BottomSheet(component.sheetControl) { SheetContent(it) }
}
```

- Decompose UI by responsibility: extract repeated items, dense branches, and reusable pieces.
- `XxxContent`: accepts component, collects content state, owns LCE, and renders screen content.
- `XxxToolbar`: may accept component when toolbar state/actions come from it.
- Small widgets: receive data and callbacks, not the component.

## AppToolbar

Use `AppToolbar` from `core.widget.toolbar`.
- For a simple toolbar, use `AppToolbar` directly in `Scaffold(topBar = { ... })`.
- If toolbar needs custom logic, or custom layout, extract it to private `XxxToolbar`.
- `AppToolbar` handles status bar padding automatically.

## State

- Collect `StateFlow`s with `by collectAsState()`.
- Collect state at the top of the composable that uses it.

## LCE
Use project LCE widgets for Replica-backed state (`LoadableState`, `PagedState`).
- `PullRefreshLceWidget`: lists, feeds, dashboards, and refreshable details.
- `LceWidget`: details/forms that load initial data but should not pull-to-refresh.
- No async loading: skip LCE.
- Pass `innerPadding` from Scaffold to `innerPadding` of LCE.
- `contentPadding` from the lambda must be applied by loaded content; it includes IME padding by
  default.
- Keep LCE inside `XxxContent`, not directly in root `XxxUi`.

```kotlin
PullRefreshLceWidget(
    state = itemsState,
    innerPadding = innerPadding,
    onRefresh = component::onRefresh
) { items, contentPadding ->
    LazyColumn(
        contentPadding = contentPadding + PaddingValues(vertical = 12.dp)
    ) {
        ...
    }
}
```

## Theme

- Use colors and typography from `CustomTheme`.
- Do not use `MaterialTheme` as a token source in feature UI.
- Prefer existing core widgets over raw Material controls when a project wrapper exists.

```kotlin
Text(
    text = title,
    color = CustomTheme.colors.text.primary,
    style = CustomTheme.typography.title.regular
)
```

## UI Utilities

- `noOverlapByMessage()`: apply to bottom bars/buttons that MessageUi popup should appear above.
- `isKeyboardVisibleAsState()`: observe IME visibility when UI state depends on it.
- `PaddingValues.plus(...)`: combine Scaffold/LCE padding with extra content padding.
- `PaddingValues.withImePadding()`: include IME bottom padding in existing padding; usually handled
  by LCE widgets.
- `SystemBars(...)`: set screen-level status/navigation bar colors or icon colors; root `ConfigureSystemBars` applies them.
- `LazyListState.TriggerLoadNext(...)`, `LazyGridState.TriggerLoadNext(...)`: trigger loading next
  pages in paged lists/grids.

## Core Widgets

- `AppToolbar`: standard screen toolbar.
- `AppToolbarButton`: standard icon-only button for toolbar back button or actions.
- `AppButton`: project button with type, loading, disabled states.
- `AppTextField`: text input; prefer the `InputControl` overload for forms.
- `AppCheckbox`: project checkbox wrapper.
- `LceWidget`, `PullRefreshLceWidget`: loading/content/error rendering.
- `EmptyPlaceholder`, `ErrorPlaceholder`, `FullscreenCircularProgress`: common states.
- `StandardDialog`, `Dialog`, `BottomSheet`: dialogs and sheets; place them after `Scaffold` at root level.

## Previews

- Always implement previews for screens and custom widgets.
- Wrap with `AppTheme`.
- Use fake components or entity `FAKE` data.
- Add multiple previews only for widgets to display meaningful visual states.

```kotlin
@Preview
@Composable
private fun XxxUiPreview() {
    AppTheme {
        XxxUi(FakeXxxComponent())
    }
}
```
