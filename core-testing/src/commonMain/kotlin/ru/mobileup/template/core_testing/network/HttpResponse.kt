package ru.mobileup.template.core_testing.network

import io.ktor.http.HttpStatusCode
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Mock HTTP response definition for test scenarios.
 *
 * Supports delayed responses to test loading states and retry behavior with virtual time.
 */
val DEFAULT_HTTP_RESPONSE_DELAY: Duration = 500.milliseconds

data class HttpResponse(
    val body: String = "",
    val status: HttpStatusCode = HttpStatusCode.OK,
    val delay: Duration = DEFAULT_HTTP_RESPONSE_DELAY,
    val headers: Map<String, String> = mapOf("Content-Type" to "application/json")
)
