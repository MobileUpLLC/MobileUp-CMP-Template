package ru.mobileup.template.core.utils

import kotlinx.coroutines.flow.MutableStateFlow

suspend fun <T> withProgress(
    progressStateFlow: MutableStateFlow<Boolean>,
    block: suspend () -> T,
): T = try {
    progressStateFlow.value = true
    block()
} finally {
    progressStateFlow.value = false
}
