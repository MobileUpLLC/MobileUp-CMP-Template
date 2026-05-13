# Example: MessageService

`MessageService` is injected into a component and called with `showMessage(Message(...))`.
Message UI is handled globally, so feature components only emit messages.

### Message Fields:
- `text: StringDesc` - Message text (required)
- `iconRes: Int?` - Optional icon drawable resource
- `actionTitle: StringDesc?` - Optional action button text
- `action: (() -> Unit)?` - Optional action button callback

## Basic Usage

```kotlin
class RealDetailsComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService,
) : ComponentContext by componentContext, DetailsComponent {

    override fun onSaveClick() {
        componentScope.safeLaunch(errorHandler) {
            // ... perform save operation
            messageService.showMessage(
                Message(
                    text = Res.string.details_save_success.resourceDesc(),
                    iconRes = CoreRes.drawable.ic_success
                )
            )
        }
    }
}
```

## With Action

```kotlin
class RealNotificationsComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService,
) : ComponentContext by componentContext, NotificationsComponent {

    fun onDismissClick() {
        componentScope.safeLaunch(errorHandler) {
            // ... perform dismiss operation
            messageService.showMessage(
                Message(
                    text = Res.string.notification_dismissed.resourceDesc(),
                    actionTitle = CoreRes.string.undo.resourceDesc(),
                    action = {
                        // Undo action
                    }
                )
            )
        }
    }
}
```
