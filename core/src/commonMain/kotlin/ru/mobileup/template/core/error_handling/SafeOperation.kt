package ru.mobileup.template.core.error_handling

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.mobileup.template.core.generated.resources.Res
import ru.mobileup.template.core.generated.resources.common_retry
import ru.mobileup.template.core.utils.StringDesc
import ru.mobileup.template.core.utils.resourceDesc

/**
 * Allows to run a function safely (with error handing).
 */
fun safeRun(
    errorHandler: ErrorHandler,
    showError: Boolean = true,
    onErrorHandled: ((e: Exception) -> Unit)? = null,
    block: () -> Unit
) {
    try {
        block()
    } catch (e: Exception) {
        errorHandler.handleError(e, showError)
        onErrorHandled?.invoke(e)
    }
}

/**
 * Allows to run a suspend function safely (with error handing).
 */
fun CoroutineScope.safeLaunch(
    errorHandler: ErrorHandler,
    showError: Boolean = true,
    onErrorHandled: ((e: Exception) -> Unit)? = null,
    block: suspend () -> Unit
): Job {
    return launch {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorHandler.handleError(e, showError)
            onErrorHandled?.invoke(e)
        }
    }
}

/**
 * Allows to run a suspend function safely (with error handing) and allows to retry a failed action.
 */
fun CoroutineScope.safeLaunchRetryable(
    errorHandler: ErrorHandler,
    onErrorHandled: ((e: Exception) -> Unit)? = null,
    retryActionTitle: StringDesc = Res.string.common_retry.resourceDesc(),
    retryAction: () -> Unit,
    block: suspend () -> Unit
): Job {
    return launch {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorHandler.handleErrorRetryable(
                exception = e,
                retryActionTitle = retryActionTitle,
                retryAction = retryAction
            )
            onErrorHandled?.invoke(e)
        }
    }
}
