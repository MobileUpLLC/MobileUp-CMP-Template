package ru.mobileup.template.core_testing.utils

/**
 * Emulates emitted outputs
 */
class OutputEmitter<T> {

    private var onOutput: ((T) -> Unit)? = null

    fun bind(onOutput: (T) -> Unit) {
        this.onOutput = onOutput
    }

    fun emit(output: T) {
        requireNotNull(onOutput) {
            "OutputEmitter is not bound. Make sure the child was created before emitting output."
        }.invoke(output)
    }
}
