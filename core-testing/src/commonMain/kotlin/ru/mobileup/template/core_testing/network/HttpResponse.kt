package ru.mobileup.template.core_testing.network

import io.ktor.http.HttpStatusCode
import kotlin.time.Duration

/**
 * Mock HTTP response definition for test scenarios.
 *
 * Supports delayed responses to test loading states and retry behavior with virtual time.
 */
data class HttpResponse(
    val body: String = "",
    val status: HttpStatusCode = HttpStatusCode.OK,
    val delay: Duration = Duration.ZERO,
    val headers: Map<String, String> = mapOf("Content-Type" to "application/json")
)
