package ru.mobileup.template.core.dialog.standard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.uikit.LocalUIViewController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert

@Composable
actual fun StandardDialog(dialogControl: StandardDialogControl) {
    val parentViewController = LocalUIViewController.current
    val slot by dialogControl.dialogSlot.collectAsState()
    val data = slot.child?.instance

    if (data != null) {
        val title = data.title.resolve()
        val message = data.message?.resolve()

        val confirmButton = data.confirmButton
        val confirmButtonText = confirmButton.text.resolve()
        val cancelButton = data.cancelButton
        val cancelButtonText = cancelButton?.text?.resolve()

        var dismissedByAction by remember(data) { mutableStateOf(false) }

        val alertController = remember(data) {
            UIAlertController.alertControllerWithTitle(
                title = title,
                message = message,
                preferredStyle = UIAlertControllerStyleAlert
            ).apply {
                addAction(
                    UIAlertAction.actionWithTitle(
                        title = confirmButtonText,
                        style = when (confirmButton.style) {
                            DialogButtonStyle.Normal -> UIAlertActionStyleDefault
                            DialogButtonStyle.Destructive -> UIAlertActionStyleDestructive
                        },
                        handler = {
                            dismissedByAction = true
                            dialogControl.dismiss()
                            confirmButton.action?.invoke()
                        }
                    )
                )

                if (cancelButton != null && cancelButtonText != null) {
                    addAction(
                        UIAlertAction.actionWithTitle(
                            title = cancelButtonText,
                            style = when (cancelButton.style) {
                                DialogButtonStyle.Normal -> UIAlertActionStyleCancel
                                DialogButtonStyle.Destructive -> UIAlertActionStyleDestructive
                            },
                            handler = {
                                dismissedByAction = true
                                dialogControl.dismiss()
                                cancelButton.action?.invoke()
                            }
                        )
                    )
                }
            }
        }

        DisposableEffect(alertController) {
            parentViewController.presentViewController(
                alertController,
                animated = true,
                completion = null
            )

            onDispose {
                if (parentViewController.presentedViewController == alertController && !dismissedByAction) {
                    alertController.dismissViewControllerAnimated(true, completion = null)
                }
            }
        }
    }
}
