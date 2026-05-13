package ru.mobileup.template.core.widget.text_field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

@Composable
internal fun SecureAppTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isPasswordVisible: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Password,
    ),
    textStyle: TextStyle = AppTextFieldDefaults.textStyle,
    labelStyle: TextStyle = AppTextFieldDefaults.labelStyle,
    colors: TextFieldColors = AppTextFieldDefaults.colors,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    moveCursorEvent: Flow<Int> = emptyFlow(),
    label: String? = null,
    placeholder: String? = null,
    shape: Shape = AppTextFieldDefaults.shape,
    interactionSource: MutableInteractionSource? = null,
) {
    val state = rememberTextFieldState(initialText = text)

    LaunchedEffect(Unit) {
        snapshotFlow(state::text)
            .map(Any::toString)
            .collect(onTextChange)
    }

    // When the text is changed not by user input, but for example, by InputControl.onValueChange()
    LaunchedEffect(text) {
        if (text != state.text) {
            state.edit {
                replace(0, length, text)
            }
        }
    }

    LaunchedEffect(moveCursorEvent) {
        moveCursorEvent.collectLatest {
            state.edit {
                if (it in 0..length) placeCursorBeforeCharAt(it)
            }
        }
    }

    SecureTextField(
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        state = state,
        enabled = isEnabled,
        isError = isError,
        textObfuscationMode = if (isPasswordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        label = label?.let {
            {
                Text(text = it, style = labelStyle)
            }
        },
        placeholder = placeholder?.let {
            {
                Text(text = it, style = textStyle)
            }
        },
        colors = colors,
        shape = shape,
        interactionSource = interactionSource
    )
}
