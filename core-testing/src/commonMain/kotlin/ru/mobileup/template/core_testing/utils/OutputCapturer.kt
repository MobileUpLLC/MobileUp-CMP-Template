package ru.mobileup.template.core_testing.utils

/**
 * Collects emitted outputs from components for assertions in tests.
 */
class OutputCapturer<T> : (T) -> Unit {
    private val _all = mutableListOf<T>()
    val all: List<T> get() = _all
    val last: T? get() = _all.lastOrNull()
    val first: T? get() = _all.firstOrNull()
    val isEmpty: Boolean get() = _all.isEmpty()

    override fun invoke(output: T) {
        _all.add(output)
    }
}
