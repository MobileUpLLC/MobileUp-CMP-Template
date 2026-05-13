package ru.mobileup.template.core.utils

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.mobileup.kmm_form_validation.control.CheckControl
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.kmm_form_validation.control.PickerControl
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.options.KeyboardCapitalization
import ru.mobileup.kmm_form_validation.options.KeyboardOptions
import ru.mobileup.kmm_form_validation.options.KeyboardType
import ru.mobileup.kmm_form_validation.options.TextTransformation
import ru.mobileup.kmm_form_validation.validation.form.FormValidator
import ru.mobileup.kmm_form_validation.validation.form.FormValidatorBuilder
import ru.mobileup.kmm_form_validation.validation.form.formValidator
import androidx.compose.foundation.text.KeyboardOptions as ComposeKeyboardOptions
import androidx.compose.ui.text.input.ImeAction as ComposeImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization as ComposeKeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType as ComposeKeyboardType

fun ComponentContext.InputControl(
    initialText: String = "",
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textTransformation: TextTransformation? = null
): InputControl = InputControl(
    componentScope,
    initialText,
    singleLine,
    maxLength,
    keyboardOptions,
    textTransformation
)

@OptIn(DelicateCoroutinesApi::class)
fun fakeInputControl(initialText: String = "") = InputControl(GlobalScope, initialText)

fun <T> ComponentContext.PickerControl(
    initialSelected: T? = null
): PickerControl<T> = PickerControl(componentScope, initialSelected)

@OptIn(DelicateCoroutinesApi::class)
fun <T> fakePickerControl(
    initialSelected: T? = null
) = PickerControl(GlobalScope, initialSelected)

fun ComponentContext.CheckControl(
    initialChecked: Boolean = false,
): CheckControl = CheckControl(componentScope, initialChecked)

@OptIn(DelicateCoroutinesApi::class)
fun fakeCheckControl(initialChecked: Boolean = false) = CheckControl(GlobalScope, initialChecked)

fun ComponentContext.formValidator(
    buildBlock: FormValidatorBuilder.() -> Unit,
): FormValidator = componentScope.formValidator(buildBlock)

@OptIn(FlowPreview::class)
fun <T> InputControl.debouncedValue(
    timeoutMillis: Long = 500L,
    transform: (String) -> T,
): StateFlow<T> = value
    .debounce { if (it.isBlank()) 0L else timeoutMillis }
    .map(transform)
    .stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = transform(value.value)
    )

fun InputControl.debouncedValue(
    timeoutMillis: Long = 500L,
): StateFlow<String> = debouncedValue(timeoutMillis) { it }

fun KeyboardOptions.toCompose(): ComposeKeyboardOptions {
    return ComposeKeyboardOptions(
        capitalization = capitalization.toCompose(),
        autoCorrectEnabled = autoCorrect,
        keyboardType = keyboardType.toCompose(),
        imeAction = imeAction.toCompose()
    )
}

private fun KeyboardCapitalization.toCompose(): ComposeKeyboardCapitalization {
    return when (this) {
        KeyboardCapitalization.None -> ComposeKeyboardCapitalization.None
        KeyboardCapitalization.Characters -> ComposeKeyboardCapitalization.Characters
        KeyboardCapitalization.Words -> ComposeKeyboardCapitalization.Words
        KeyboardCapitalization.Sentences -> ComposeKeyboardCapitalization.Sentences
    }
}

private fun KeyboardType.toCompose(): ComposeKeyboardType {
    return when (this) {
        KeyboardType.Text -> ComposeKeyboardType.Text
        KeyboardType.Ascii -> ComposeKeyboardType.Ascii
        KeyboardType.Email -> ComposeKeyboardType.Email
        KeyboardType.Uri -> ComposeKeyboardType.Uri
        KeyboardType.Number -> ComposeKeyboardType.Number
        KeyboardType.NumberPassword -> ComposeKeyboardType.NumberPassword
        KeyboardType.Password -> ComposeKeyboardType.Password
        KeyboardType.Phone -> ComposeKeyboardType.Phone
    }
}

private fun ImeAction.toCompose(): ComposeImeAction {
    return when (this) {
        ImeAction.Default -> ComposeImeAction.Default
        ImeAction.None -> ComposeImeAction.None
        ImeAction.Search -> ComposeImeAction.Search
        ImeAction.Go -> ComposeImeAction.Go
        ImeAction.Done -> ComposeImeAction.Done
        ImeAction.Next -> ComposeImeAction.Next
        ImeAction.Send -> ComposeImeAction.Send
        ImeAction.Previous -> ComposeImeAction.Previous
    }
}
