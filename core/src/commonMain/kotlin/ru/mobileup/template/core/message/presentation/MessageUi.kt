package ru.mobileup.template.core.message.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.painterResource
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import kotlin.math.roundToInt

/**
 * Displays a [Message] as a popup at the bottom of screen.
 */
@Composable
fun MessageUi(
    component: MessageComponent,
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val visibleMessage by component.visibleMessage.collectAsState()

    val bottomPaddingPx = with(LocalDensity.current) { bottomPadding.toPx().roundToInt() }
    val additionalBottomPaddingPx = ((LocalMessageOffsets.current.values.maxOrNull() ?: 0))

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        visibleMessage?.let {
            MessagePopup(
                message = it,
                bottomPaddingPx = bottomPaddingPx + additionalBottomPaddingPx,
                onAction = component::onActionClick
            )
        }
    }
}

@Composable
private fun MessagePopup(
    message: Message,
    onAction: () -> Unit,
    bottomPaddingPx: Int
) {
    Popup(
        offset = IntOffset(0, -bottomPaddingPx),
        alignment = Alignment.BottomCenter,
        properties = PopupProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = CustomTheme.colors.background.toast),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .wrapContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(vertical = 13.dp, horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                message.iconRes?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        tint = CustomTheme.colors.icon.invert
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = message.text.resolve(),
                    color = CustomTheme.colors.text.invert,
                    style = CustomTheme.typography.body.regular
                )
                message.actionTitle?.let {
                    MessageButton(text = it.resolve(), onClick = onAction)
                }
            }
        }
    }
}

@Composable
private fun MessageButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp),
        text = text,
        style = CustomTheme.typography.button.bold,
        color = CustomTheme.colors.button.primary
    )
}

@Preview
@Composable
private fun MessageUiPreview() {
    AppTheme {
        MessageUi(FakeMessageComponent(), 40.dp)
    }
}
