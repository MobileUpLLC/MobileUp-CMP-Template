---
name: string-resources
description: Compact guide for using the project StringDesc wrapper over Compose Resources
---

## StringDesc

StringDesc is a universal wrapper for different kinds of text values.
It keeps them in a deferred form and resolves them to a plain String only at the point of use.

## Creating StringDesc

For resources from the same module, import:

```kotlin
import <package>.features.generated.resources.Res
import <package>.features.generated.resources.some_string
```

For resources from another module, import with an alias:

```kotlin
import <package>.core.generated.resources.Res as CoreRes
import <package>.core.generated.resources.some_string
```

### `resourceDesc`

Use `.resourceDesc(...)` for regular string resources, with or without format arguments.

```kotlin
import <package>.core.utils.StringDesc
import <package>.core.utils.resourceDesc

val title: StringDesc = Res.string.profile_title.resourceDesc()
val subtitle: StringDesc = Res.string.profile_subtitle.resourceDesc(userName)
val retry: StringDesc = CoreRes.string.common_retry.resourceDesc()
```

Arguments may also be `StringDesc`.

```kotlin
val save = CoreRes.string.common_save.resourceDesc()
val subtitle = Res.string.profile_action.resourceDesc(save)
```

### `pluralDesc`

Use `.pluralDesc(...)` for plural resources.

```kotlin
import <package>.core.utils.pluralDesc

val itemsCount = Res.plurals.items_count.pluralDesc(count, count)
```

### `desc`

Use `.desc()` only for already resolved text from external, debug, mock, or fake sources.

```kotlin
import <package>.core.utils.desc

val serverMessage = response.message.desc()
```

### `plus`

Use `+` to concatenate `StringDesc` values.

```kotlin
import <package>.core.utils.desc
import <package>.core.utils.plus

val itemsCount = Res.plurals.items_count.pluralDesc(count, count)
val subtitle = Res.string.selected_items.resourceDesc() + ": ".desc() + itemsCount
```

### `joinToStringDesc`

Use `.joinToStringDesc()` to join a list of `StringDesc`. You may pass a custom separator.

```kotlin
import <package>.core.utils.joinToStringDesc
        
val title = labels.joinToStringDesc(separator = " • ")
```

## Resolving StringDesc

### `resolve`

Use `.resolve()` in composables only.

```kotlin
Text(text = title.resolve())
Text(text = errorMessage.resolve())
```

### `resolveString`

Use `.resolveString()` outside Compose when a plain `String` is required.

```kotlin
suspend fun buildShareText(title: StringDesc): String {
    return title.resolveString()
}
```
