package ru.mobileup.template.core.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun createDefaultJson(): Json = Json {
    explicitNulls = false
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
}