package ru.mobileup.template.core.dialog.standard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ru.mobileup.template.core.theme.custom.CustomTheme

@Composable
actual fun StandardDialog(dialogControl: StandardDialogControl) {
    val dismissableByUser by dialogControl.dismissableByUser.collectAsState()
    val slot by dialogControl.dialogSlot.collectAsState()

    slot.child?.let {
        val data = it.instance

        AlertDialog(
            onDismissRequest = dialogControl::dismiss,
            properties = DialogProperties(
                dismissOnBackPress = dismissableByUser,
                dismissOnClickOutside = dismissableByUser
            ),
            shape = RoundedCornerShape(4.dp),

            title = {
                Text(
                    text = data.title.resolve(),
                    style = CustomTheme.typography.title.regular,
                    color = CustomTheme.colors.text.primary
                )
            },

            text = data.message?.let {
                {
                    Text(
                        text = data.message.resolve(),
                        style = CustomTheme.typography.body.regular,
                        color = CustomTheme.colors.text.primary
                    )
                }
            },

            confirmButton = data.confirmButton.let { button ->
                {
                    DialogButton(
                        text = button.text.resolve(),
                        style = button.style,
                        onClick = {
                            dialogControl.dismiss()
                            button.action?.invoke()
                        }
                    )
                }
            },

            dismissButton = data.cancelButton?.let { button ->
                {
                    DialogButton(
                        text = button.text.resolve(),
                        style = button.style,
                        onClick = {
                            dialogControl.dismiss()
                            button.action?.invoke()
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun DialogButton(
    text: String,
    style: DialogButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp)
            .widthIn(min = 60.dp),
        text = text,
        style = CustomTheme.typography.button.regular,
        color = when (style) {
            DialogButtonStyle.Normal -> CustomTheme.colors.text.primary
            DialogButtonStyle.Destructive -> CustomTheme.colors.text.error
        },
        textAlign = TextAlign.Center
    )
}
