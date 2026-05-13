# Example: Dialogs

Shows the three dialog patterns used in components.

---

## 1. StandardDialogControl

Use for confirmation and alert dialogs.

```kotlin
interface SettingsComponent {
    val confirmDeleteDialog: StandardDialogControl

    fun onDeleteAccountClick()
}

class RealSettingsComponent(
    componentContext: ComponentContext,
    private val onOutput: (SettingsComponent.Output) -> Unit
) : ComponentContext by componentContext, SettingsComponent {

    override val confirmDeleteDialog = standardDialogControl(key = "confirmDeleteDialog")

    override fun onDeleteAccountClick() {
        confirmDeleteDialog.show(
            StandardDialogData(
                title = Res.string.settings_delete_account_title.resourceDesc(),
                message = Res.string.settings_delete_account_message.resourceDesc(),
                confirmButton = DialogButton(
                    text = CoreRes.string.confirm.resourceDesc(),
                    action = { onOutput(SettingsComponent.Output.AccountDeleted) }
                ),
                dismissButton = DialogButton(
                    text = CoreRes.string.cancel.resourceDesc()
                )
            )
        )
    }
}

class FakeSettingsComponent : SettingsComponent {
    override val confirmDeleteDialog = fakeStandardDialogControl()

    override fun onDeleteAccountClick() = Unit
}

@Composable
fun SettingsUi(component: SettingsComponent) {
    StandardDialog(component.confirmDeleteDialog)
}
```

---

## 2. SimpleDialogControl

Use when the dialog renders plain data and does not need a dedicated child component.

```kotlin
interface ProfileComponent {
    val infoDialog: SimpleDialogControl<InfoDialogData>

    fun onInfoClick()
}

data class InfoDialogData(
    val title: StringDesc,
    val message: StringDesc
) {
    companion object {
        val MOCK = InfoDialogData(
            title = "Why we need this permission".desc(),
            message = "We use it to prefill your profile data.".desc()
        )
    }
}

class RealProfileComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, ProfileComponent {

    override val infoDialog = simpleDialogControl(key = "infoDialog")

    override fun onInfoClick() {
        infoDialog.show(InfoDialogData.MOCK)
    }
}

class FakeProfileComponent : ProfileComponent {
    override val infoDialog = fakeSimpleDialogControl(InfoDialogData.MOCK)

    override fun onInfoClick() = Unit
}

@Composable
fun ProfileUi(component: ProfileComponent) {
    Dialog(component.infoDialog) { data ->
        InfoDialogContent(
            title = data.title.resolve(),
            message = data.message.resolve(),
            onDismiss = component.infoDialog::dismiss
        )
    }
}
```

Use `BottomSheet(component.infoDialog) { data -> ... }` when the same plain data should be shown as
a bottom sheet.

---

## 3. DialogControl<C, T>

Use when the dialog needs its own child component with state, logic, and `Output`.

```kotlin
interface ProfileComponent {
    val howToDialogControl: DialogControl<HowToComponent.Config, HowToComponent>

    fun onHowToClick()
}

class RealProfileComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, ProfileComponent {

    override val howToDialogControl = dialogControl<HowToComponent.Config, HowToComponent>(
        key = "howToDialog",
        dialogComponentFactory = { config, context, _ ->
            componentFactory.createHowToComponent(
                componentContext = context,
                config = config,
                onOutput = ::onHowToOutput
            )
        },
        serializer = HowToComponent.Config.serializer()
    )

    override fun onHowToClick() {
        howToDialogControl.show(HowToComponent.Config(/* ... */))
    }

    private fun onHowToOutput(output: HowToComponent.Output) {
        // Handle output
    }
}

class FakeProfileComponent : ProfileComponent {
    override val howToDialogControl = fakeDialogControl(FakeHowToComponent())

    override fun onHowToClick() = Unit
}

@Composable
fun ProfileUi(component: ProfileComponent) {
    BottomSheet(component.howToDialogControl) { dialogComponent ->
        HowToUi(component = dialogComponent)
    }
}
```

Use
`Dialog(component.howToDialogControl) { dialogComponent -> ... }` when the dialog component should be shown as a dialog.
---

## Serializer

Serialization is optional. Use it when dialog state should be restored on Android after process
recreation, for example for an important non-dismissible dialog.

`StandardDialogControl` does not support serialization because `StandardDialogData` contains
non-serializable lambdas.

---

## dismissableByUser

`dismissableByUser` controls whether the user can hide the dialog or bottom sheet manually. Default is `true`.

---

## Cheat Sheet

- `StandardDialogControl` - confirm or alert dialog
- `SimpleDialogControl<T>` - plain data
- `DialogControl<C, T>` - dedicated child component
