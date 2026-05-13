package ru.mobileup.template.core_testing.network

import io.ktor.http.HttpMethod
import io.ktor.http.Url

/**
 * Simplified HTTP request model used by [MockServer].
 *
 * Keeps only the data needed by tests to match requests and assert what was sent.
 */
data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val fullUrl: String,
    val body: String,
    val headers: Map<String, String>
) {

    /**
     * Returns a query parameter value from [fullUrl] if present.
     */
    fun queryParam(name: String): String? = Url(fullUrl).parameters[name]
}
