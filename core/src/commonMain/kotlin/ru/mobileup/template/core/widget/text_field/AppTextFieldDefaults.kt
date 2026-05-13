package ru.mobileup.template.core.widget.text_field

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.theme.custom.CustomTheme

@Immutable
enum class TextFieldType {
    Common,
    Secure
}

@Immutable
object AppTextFieldDefaults {

    @Stable
    val shape = RoundedCornerShape(28.dp)

    @Stable
    val textStyle: TextStyle
        @Composable
        get() = CustomTheme.typography.body.regular

    @Stable
    val labelStyle: TextStyle
        @Composable
        get() = CustomTheme.typography.body.regular

    @Stable
    val colors: TextFieldColors
        @Composable
        get() = TextFieldDefaults.colors(
            focusedTextColor = CustomTheme.colors.text.primary,
            unfocusedTextColor = CustomTheme.colors.text.primary,
            disabledTextColor = CustomTheme.colors.text.primaryDisabled,
            errorTextColor = CustomTheme.colors.text.primary,

            focusedContainerColor = CustomTheme.colors.textField.background,
            unfocusedContainerColor = CustomTheme.colors.textField.background,
            disabledContainerColor = CustomTheme.colors.textField.backgroundDisabled,
            errorContainerColor = CustomTheme.colors.textField.background,

            focusedPlaceholderColor = CustomTheme.colors.text.secondary,
            unfocusedPlaceholderColor = CustomTheme.colors.text.secondary,
            disabledPlaceholderColor = CustomTheme.colors.text.primaryDisabled,
            errorPlaceholderColor = CustomTheme.colors.text.secondary,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,

            focusedSupportingTextColor = CustomTheme.colors.text.primary,
            unfocusedSupportingTextColor = CustomTheme.colors.text.primary,
            disabledSupportingTextColor = CustomTheme.colors.text.primaryDisabled,
            errorSupportingTextColor = CustomTheme.colors.text.error,

            focusedLabelColor = CustomTheme.colors.text.secondary,
            unfocusedLabelColor = CustomTheme.colors.text.secondary,
            disabledLabelColor = CustomTheme.colors.text.secondaryDisabled,
            errorLabelColor = CustomTheme.colors.text.error,

            focusedPrefixColor = CustomTheme.colors.text.primary,
            unfocusedPrefixColor = CustomTheme.colors.text.primary,
            disabledPrefixColor = CustomTheme.colors.text.primaryDisabled,
            errorPrefixColor = CustomTheme.colors.text.primary,

            focusedSuffixColor = CustomTheme.colors.text.primary,
            unfocusedSuffixColor = CustomTheme.colors.text.primary,
            disabledSuffixColor = CustomTheme.colors.text.primaryDisabled,
            errorSuffixColor = CustomTheme.colors.text.primary,

            focusedLeadingIconColor = CustomTheme.colors.icon.primary,
            unfocusedLeadingIconColor = CustomTheme.colors.icon.primary,
            disabledLeadingIconColor = CustomTheme.colors.icon.primaryDisabled,
            errorLeadingIconColor = CustomTheme.colors.icon.error,

            focusedTrailingIconColor = CustomTheme.colors.icon.primary,
            unfocusedTrailingIconColor = CustomTheme.colors.icon.primary,
            disabledTrailingIconColor = CustomTheme.colors.icon.primaryDisabled,
            errorTrailingIconColor = CustomTheme.colors.icon.error,

            cursorColor = CustomTheme.colors.icon.primary,
            errorCursorColor = CustomTheme.colors.icon.error,

            selectionColors = TextSelectionColors(
                handleColor = CustomTheme.colors.icon.primary,
                backgroundColor = CustomTheme.colors.text.primary.copy(alpha = 0.4f)
            ),
        )

    @Stable
    private val defaultBorderWidth = 1.dp

    @Stable
    @Composable
    fun border(isError: Boolean, hasFocus: Boolean, width: Dp = defaultBorderWidth): BorderStroke {
        val color by animateColorAsState(
            targetValue = when {
                isError -> CustomTheme.colors.border.error
                hasFocus -> CustomTheme.colors.border.primary
                else -> Color.Transparent
            },
            label = "animated border color"
        )
        return BorderStroke(width, color)
    }

    @Stable
    val defaultKeyboardActions: KeyboardActions
        @Composable
        get() = LocalFocusManager.current.run {
            KeyboardActions(onDone = { clearFocus() })
        }
}
