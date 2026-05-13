package ru.mobileup.template.core.widget.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.theme.AppTheme

@Composable
fun AppButton(
    buttonType: ButtonType,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    textStyle: TextStyle = AppButtonDefaults.textStyle,
    colors: ButtonColors = AppButtonDefaults.colors(buttonType),
    border: BorderStroke = AppButtonDefaults.border(buttonType, isEnabled),
    shape: Shape = AppButtonDefaults.buttonShape,
    elevation: Dp = AppButtonDefaults.elevation(buttonType, isEnabled, interactionSource),
    contentPadding: PaddingValues = AppButtonDefaults.contentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor = if (isEnabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (isEnabled) colors.contentColor else colors.disabledContentColor

    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides LocalTextStyle.current.merge(textStyle)
    ) {
        Row(
            modifier
                .semantics { role = Role.Button }
                .then(
                    if (elevation > 0.dp) {
                        Modifier.shadow(elevation, shape)
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (border.width > 0.dp) {
                        Modifier.border(border, shape)
                    } else {
                        Modifier
                    }
                )
                .background(containerColor, shape)
                .clip(shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            enabled = isEnabled,
                            onClick = onClick,
                            interactionSource = interactionSource
                        )
                    } else {
                        Modifier
                    }
                )
                .defaultMinSize(
                    minWidth = AppButtonDefaults.MinWidth,
                    minHeight = AppButtonDefaults.MinHeight,
                )
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun AppButton(
    text: String,
    buttonType: ButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    textStyle: TextStyle = AppButtonDefaults.textStyle,
    colors: ButtonColors = AppButtonDefaults.colors(buttonType),
    border: BorderStroke = AppButtonDefaults.border(buttonType, isEnabled),
    shape: Shape = AppButtonDefaults.buttonShape,
    elevation: Dp = AppButtonDefaults.elevation(buttonType, isEnabled, interactionSource),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    isLoading: Boolean = false,
) {
    AppButton(
        modifier = modifier,
        buttonType = buttonType,
        onClick = onClick.takeUnless { isLoading },
        interactionSource = interactionSource,
        isEnabled = isEnabled,
        textStyle = textStyle,
        colors = colors,
        shape = shape,
        border = border,
        elevation = elevation,
        contentPadding = contentPadding,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = LocalContentColor.current
            )
        } else {
            Text(text)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Button Primary",
                buttonType = ButtonType.Primary,
                onClick = {},
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Button Secondary",
                buttonType = ButtonType.Secondary,
                onClick = {},
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = "",
                buttonType = ButtonType.Primary,
                onClick = {},
                isLoading = true,
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = "",
                buttonType = ButtonType.Secondary,
                onClick = {},
                isLoading = true
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Button Primary",
                buttonType = ButtonType.Primary,
                onClick = {},
                isEnabled = false
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Button Secondary",
                buttonType = ButtonType.Secondary,
                onClick = {},
                isEnabled = false
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonType = ButtonType.Primary,
                onClick = {},
                contentPadding = PaddingValues(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add")
            }
        }
    }
}
