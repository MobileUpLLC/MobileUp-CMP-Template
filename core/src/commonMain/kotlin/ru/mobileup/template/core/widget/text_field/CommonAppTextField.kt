package ru.mobileup.template.core.widget.text_field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun CommonAppTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    prefix: String? = null,
    suffix: String? = null,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = minLines == 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = AppTextFieldDefaults.defaultKeyboardActions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = AppTextFieldDefaults.shape,
    colors: TextFieldColors = AppTextFieldDefaults.colors,
    textStyle: TextStyle = AppTextFieldDefaults.textStyle,
    labelStyle: TextStyle = AppTextFieldDefaults.labelStyle,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    moveCursorEvent: Flow<Int> = emptyFlow(),
    label: String? = null,
    placeholder: String? = null,
) {
    val currentValue by rememberUpdatedState(text)
    var currentSelection by rememberSaveable(stateSaver = TextRangeSaver) {
        mutableStateOf(TextRange(0))
    }
    var currentComposition by rememberSaveable(stateSaver = NullableTextRangeSaver) {
        mutableStateOf(null)
    }
    val currentTextFieldValue by remember {
        derivedStateOf {
            TextFieldValue(currentValue, currentSelection, currentComposition)
        }
    }

    LaunchedEffect(moveCursorEvent) {
        moveCursorEvent.collectLatest {
            currentSelection = TextRange(it)
        }
    }

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = currentTextFieldValue,
        onValueChange = {
            onTextChange(it.text)
            currentSelection = it.selection
            currentComposition = it.composition
        },
        enabled = isEnabled,
        readOnly = !isEnabled,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
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
        prefix = prefix?.let {
            {
                Text(text = it, style = textStyle)
            }
        },
        suffix = suffix?.let {
            {
                Text(text = it, style = textStyle)
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        isError = isError,
        colors = colors,
    )
}

private val TextRangeSaver = listSaver(
    save = { listOf(it.start, it.end) },
    restore = { TextRange(it[0], it[1]) }
)

private val NullableTextRangeSaver = listSaver<TextRange?, Int>(
    save = { if (it != null) listOf(it.start, it.end) else emptyList() },
    restore = { TextRange(it[0], it[1]) }
)
