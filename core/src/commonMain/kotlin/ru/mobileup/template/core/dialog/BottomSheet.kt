package ru.mobileup.template.core.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.theme.custom.CustomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> BottomSheet(
    dialogControl: DialogControl<*, T>,
    modifier: Modifier = Modifier,
    skipPartiallyExpanded: Boolean = true,
    backgroundColor: Color = CustomTheme.colors.background.screen,
    cornerRadius: Dp = 10.dp,
    content: @Composable (T) -> Unit,
) {
    val dismissableByUser by dialogControl.dismissableByUser.collectAsState()
    val dismissableByUserState = rememberUpdatedState(dismissableByUser)

    val dialogSlot by dialogControl.dialogSlot.collectAsState()
    val targetComponent = dialogSlot.child?.instance
    var visibleComponent by remember(dialogControl) { mutableStateOf<T?>(null) }
    val isHidingDismissedSlotState = rememberUpdatedState(
        targetComponent == null && visibleComponent != null
    )

    val confirmValueChange = remember<(SheetValue) -> Boolean>(dialogControl) {
        { value ->
            when {
                value != SheetValue.Hidden -> true

                isHidingDismissedSlotState.value -> true

                dismissableByUserState.value -> {
                    dialogControl.dismiss()
                    true
                }

                else -> false
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange
    )

    LaunchedEffect(targetComponent) {
        if (targetComponent != null) {
            val wasSheetRetained = visibleComponent != null
            visibleComponent = targetComponent
            if (wasSheetRetained && sheetState.targetValue == SheetValue.Hidden) {
                // ModalBottomSheet runs its internal show effect only when it enters composition.
                // Retained sheets stay mounted during hide, so reopen them explicitly.
                sheetState.show()
            }
            return@LaunchedEffect
        }

        if (visibleComponent == null) return@LaunchedEffect

        try {
            sheetState.hide()
        } finally {
            // hide() can be cancelled by a quick reopen or by leaving composition.
            // Remove the sheet only if the dialog slot is still closed.
            if (dialogControl.dialogSlot.value.child == null) {
                visibleComponent = null
            }
        }
    }

    val component = visibleComponent
    if (component != null) {
        ModalBottomSheet(
            modifier = modifier.statusBarsPadding(),
            onDismissRequest = { if (dismissableByUser) dialogControl.dismiss() },
            properties = ModalBottomSheetProperties(dismissableByUser),
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
            containerColor = backgroundColor,
            scrimColor = Color.Black.copy(alpha = 0.4f),
            dragHandle = null,
        ) {
            Box(
                modifier = Modifier.imePadding()
            ) {
                content(component)
            }
        }
    }
}
