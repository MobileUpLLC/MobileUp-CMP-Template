package ru.mobileup.template.core.widget.button

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.theme.custom.CustomTheme

@Immutable
enum class ButtonType {
    Primary,
    Secondary
}

@Immutable
object AppButtonDefaults {

    @Stable
    val buttonShape = RoundedCornerShape(percent = 50)

    @Stable
    val contentPadding = PaddingValues(16.dp)

    @Stable
    val textStyle: TextStyle
        @Composable
        get() = CustomTheme.typography.button.bold

    @Stable
    @Composable
    fun colors(buttonType: ButtonType): ButtonColors = ButtonColors(
        containerColor = containerColor(buttonType),
        contentColor = contentColor(buttonType),
        disabledContainerColor = disabledContainerColor(buttonType),
        disabledContentColor = disabledContentColor(buttonType),
    )

    @Stable
    @Composable
    fun containerColor(buttonType: ButtonType): Color = when (buttonType) {
        ButtonType.Primary -> CustomTheme.colors.button.primary
        ButtonType.Secondary -> CustomTheme.colors.button.secondary
    }

    @Stable
    @Composable
    fun contentColor(buttonType: ButtonType): Color = when (buttonType) {
        ButtonType.Primary -> CustomTheme.colors.text.invert
        ButtonType.Secondary -> CustomTheme.colors.text.primary
    }

    @Stable
    @Composable
    fun disabledContainerColor(buttonType: ButtonType): Color = when (buttonType) {
        ButtonType.Primary -> CustomTheme.colors.button.primaryDisabled
        ButtonType.Secondary -> CustomTheme.colors.button.secondaryDisabled
    }

    @Stable
    @Composable
    fun disabledContentColor(buttonType: ButtonType): Color = when (buttonType) {
        ButtonType.Primary -> CustomTheme.colors.text.invertDisabled
        ButtonType.Secondary -> CustomTheme.colors.text.primaryDisabled
    }

    @Stable
    @Composable
    fun border(buttonType: ButtonType, isEnabled: Boolean): BorderStroke = when (buttonType) {
        ButtonType.Secondary -> BorderStroke(
            1.dp,
            CustomTheme.colors.border.primary.copy(alpha = if (isEnabled) 1f else 0.4f)
        )

        else -> BorderStroke(0.dp, Color.Transparent)
    }

    @Stable
    @Composable
    fun elevation(
        buttonType: ButtonType,
        isEnabled: Boolean,
        interactionSource: InteractionSource
    ): Dp {
        val defaultElevation = when (buttonType) {
            ButtonType.Primary -> 5.dp
            ButtonType.Secondary -> 0.dp
        }

        if (defaultElevation == 0.dp) return 0.dp

        val isPressed by interactionSource.collectIsPressedAsState()
        return animateDpAsState(
            targetValue = when {
                !isEnabled -> 0.dp
                isPressed -> 1.dp
                else -> defaultElevation
            }
        ).value
    }

    val MinWidth = 58.dp
    val MinHeight = 40.dp
}
