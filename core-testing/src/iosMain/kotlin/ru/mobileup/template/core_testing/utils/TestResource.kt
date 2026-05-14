package ru.mobileup.template.core_testing.utils

actual suspend fun readTestResource(path: String): String {
    error("Test resources are supported only in Android host tests")
}
