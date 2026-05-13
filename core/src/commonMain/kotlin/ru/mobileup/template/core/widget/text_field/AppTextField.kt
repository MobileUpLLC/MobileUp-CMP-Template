package ru.mobileup.template.core.widget.text_field

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.kmm_form_validation.options.OnlyDigitsTextTransformation
import ru.mobileup.kmm_form_validation.validation.control.ValidationError
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.StringDesc
import ru.mobileup.template.core.utils.desc
import ru.mobileup.template.core.utils.isKeyboardVisibleAsState
import ru.mobileup.template.core.utils.toCompose
import ru.mobileup.kmm_form_validation.options.KeyboardOptions as KmmKeyboardOptions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.Common,
    onFocusChange: (Boolean) -> Unit = {},
    isEnabled: Boolean = true,
    supportingText: String? = null,
    errorText: String? = null,
    headerText: String? = null,
    prefix: String? = null,
    suffix: String? = null,
    isError: Boolean = false,
    isPasswordVisible: Boolean = false,
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
    hasFocus: Boolean = false,
    border: BorderStroke = AppTextFieldDefaults.border(isError, hasFocus),
    scrollToItEvent: Flow<Unit> = emptyFlow(),
    moveCursorEvent: Flow<Int> = emptyFlow(),
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val isKeyboardVisible by isKeyboardVisibleAsState()

    LaunchedEffect(scrollToItEvent) {
        scrollToItEvent.collectLatest {
            bringIntoViewRequester.bringIntoView()
        }
    }

    LaunchedEffect(hasFocus, isKeyboardVisible) {
        if (hasFocus) {
            focusRequester.requestFocus()
            if (isKeyboardVisible) {
                delay(30) // Wait for the keyboard to fully open before bringing the text field into view
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    Column(
        modifier = modifier
            .animateContentSize()
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        headerText?.let {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = it,
                color = CustomTheme.colors.text.primary,
                style = CustomTheme.typography.title.regular
            )
        }

        val textFieldModifier = Modifier
            .border(border, shape)
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChange(it.isFocused) }

        when (type) {
            TextFieldType.Common -> CommonAppTextField(
                modifier = textFieldModifier,
                text = text,
                onTextChange = onTextChange,
                isEnabled = isEnabled,
                prefix = prefix,
                suffix = suffix,
                isError = isError,
                minLines = minLines,
                maxLines = maxLines,
                singleLine = singleLine,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
                textStyle = textStyle,
                labelStyle = labelStyle,
                moveCursorEvent = moveCursorEvent,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
            )

            TextFieldType.Secure -> SecureAppTextField(
                modifier = textFieldModifier,
                text = text,
                onTextChange = onTextChange,
                isPasswordVisible = isPasswordVisible,
                shape = shape,
                textStyle = textStyle,
                labelStyle = labelStyle,
                colors = colors,
                leadingIcon = leadingIcon,
                moveCursorEvent = moveCursorEvent,
                label = label,
                placeholder = placeholder,
                interactionSource = interactionSource,
                isError = isError,
                isEnabled = isEnabled,
                keyboardOptions = keyboardOptions,
                trailingIcon = trailingIcon
            )
        }

        AnimatedContent(
            targetState = errorText?.let { it to true } ?: (supportingText to false),
            transitionSpec = {
                slideInVertically { it } togetherWith slideOutVertically { -2 * it }
            }
        ) { (textToDisplay, isError) ->
            if (!textToDisplay.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    text = textToDisplay,
                    color = if (isError) CustomTheme.colors.text.error else CustomTheme.colors.text.primary,
                    style = CustomTheme.typography.caption.regular
                )
            }
        }
    }
}

@Composable
fun AppTextField(
    inputControl: InputControl,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.Common,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    isPasswordVisible: Boolean = false,
    keyboardActions: KeyboardActions = AppTextFieldDefaults.defaultKeyboardActions,
    colors: TextFieldColors = AppTextFieldDefaults.colors,
    textStyle: TextStyle = AppTextFieldDefaults.textStyle,
    labelStyle: TextStyle = AppTextFieldDefaults.labelStyle,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: BorderStroke? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    headerText: String? = null,
    prefix: String? = null,
    suffix: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val hasFocus by inputControl.hasFocus.collectAsState()
    val error by inputControl.error.collectAsState()
    val enabled by inputControl.enabled.collectAsState()
    val text by inputControl.value.collectAsState()

    AppTextField(
        modifier = modifier,
        type = type,
        text = text,
        label = label,
        placeholder = placeholder,
        isError = error != null,
        isPasswordVisible = isPasswordVisible,
        supportingText = supportingText,
        headerText = headerText,
        errorText = error?.resolveText(),
        prefix = prefix,
        suffix = suffix,
        isEnabled = enabled,
        onTextChange = inputControl::onValueChange,
        onFocusChange = inputControl::onFocusChange,
        singleLine = minLines == 1,
        keyboardOptions = inputControl.keyboardOptions.toCompose(),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        hasFocus = hasFocus,
        scrollToItEvent = inputControl.scrollToItEvent,
        moveCursorEvent = inputControl.moveCursorEvent,
        minLines = minLines,
        maxLines = maxLines,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = colors,
        textStyle = textStyle,
        labelStyle = labelStyle,
        border = border ?: AppTextFieldDefaults.border(
            isError = error != null,
            hasFocus = hasFocus
        ),
        interactionSource = interactionSource,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    val scope = rememberCoroutineScope()
    val inputControl = InputControl(scope).apply {
        requestFocus()
    }
    val inputControl2 = InputControl(
        coroutineScope = scope,
        initialText = "Some text",
        keyboardOptions = KmmKeyboardOptions()
    ).apply {
        error.value = "Error".desc()
    }
    val inputControl3 = InputControl(
        coroutineScope = scope,
        initialText = "Some text",
        keyboardOptions = KmmKeyboardOptions()
    ).apply {
        enabled.value = false
    }
    val inputControl4 = InputControl(scope)
    val inputControl5 = InputControl(
        coroutineScope = scope,
        textTransformation = OnlyDigitsTextTransformation(),
    )
    val inputControl6 = InputControl(
        coroutineScope = scope,
        initialText = "Some text",
        keyboardOptions = KmmKeyboardOptions()
    ).apply {
        error.value = "Error".desc()
    }
    val inputControl7 = InputControl(
        coroutineScope = scope,
        initialText = "1234567890",
        keyboardOptions = KmmKeyboardOptions()
    )
    val inputControl8 = InputControl(
        coroutineScope = scope,
        initialText = "1234567890",
        keyboardOptions = KmmKeyboardOptions()
    )

    AppTheme {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = inputControl2,
                label = "Label",
                placeholder = "Placeholder",
                supportingText = "Footer",
                headerText = "Header",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                }
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = inputControl,
                label = "Заголовок",
                placeholder = "Текст",
                supportingText = "Supporting",
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = inputControl4,
                label = "Заголовок",
                placeholder = "Текст",
                minLines = 3,
                maxLines = 3,
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = inputControl6,
                label = "Заголовок",
                placeholder = "Текст"
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = inputControl3,
                label = "Заголовок",
                placeholder = "Текст"
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = inputControl5,
                placeholder = "000 000 00 00",
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                type = TextFieldType.Secure,
                inputControl = inputControl7,
                label = "Password",
            )
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                type = TextFieldType.Secure,
                inputControl = inputControl8,
                label = "Password",
                isPasswordVisible = true
            )
        }
    }
}

@Composable
private fun ValidationError.resolveText(): String {
    return when (this) {
        is StringDesc -> this.resolve()
        else -> error("Unsupported ValidationError type")
    }
}
