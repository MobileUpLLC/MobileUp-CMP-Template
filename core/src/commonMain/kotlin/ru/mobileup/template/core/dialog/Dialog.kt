package ru.mobileup.template.core.dialog

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> Dialog(
    dialogControl: DialogControl<*, T>,
    content: @Composable (T) -> Unit
) {
    val dismissableByUser by dialogControl.dismissableByUser.collectAsState()
    val slot by dialogControl.dialogSlot.collectAsState()

    slot.child?.let {
        BasicAlertDialog(
            onDismissRequest = { dialogControl.dismiss() },
            properties = DialogProperties(
                dismissOnBackPress = dismissableByUser,
                dismissOnClickOutside = dismissableByUser
            )
        ) {
            content(it.instance)
        }
    }
}