package ru.mobileup.template.features.utils

actual suspend fun readTestResource(path: String): String {
    val classLoader = Thread.currentThread().contextClassLoader
        ?: error("Context class loader is not available")

    val inputStream = classLoader.getResourceAsStream(path)
        ?: error("Test resource is not found: $path")

    return inputStream.use { it.readBytes().decodeToString() }
}
