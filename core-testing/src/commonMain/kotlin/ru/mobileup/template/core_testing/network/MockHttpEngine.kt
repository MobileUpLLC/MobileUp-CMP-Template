package ru.mobileup.template.core_testing.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.http.Headers
import io.ktor.http.content.TextContent
import io.ktor.http.headers
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Creates a Ktor [MockEngine] backed by [MockServer].
 *
 * All requests are converted to [HttpRequest], delegated to the mock server,
 * and transformed back to Ktor responses.
 */
fun createMockHttpEngine(
    mockServer: MockServer,
    dispatcher: CoroutineDispatcher
): HttpClientEngine {
    return MockEngine(
        MockEngineConfig().apply {
            this.dispatcher = dispatcher
            addHandler { requestData ->
                val request = HttpRequest(
                    method = requestData.method,
                    path = requestData.url.encodedPath,
                    fullUrl = requestData.url.toString(),
                    body = (requestData.body as? TextContent)?.text ?: "",
                    headers = requestData.headers.toMap()
                )
                val mockResponse = mockServer.handleRequest(request)
                respond(
                    content = mockResponse.body,
                    status = mockResponse.status,
                    headers = headers {
                        mockResponse.headers.forEach { (k, v) -> append(k, v) }
                    }
                )
            }
        }
    )
}

/**
 * Converts Ktor [Headers] to a plain map for easier assertions in tests.
 */
private fun Headers.toMap(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    forEach { key, values -> result[key] = values.joinToString() }
    return result
}
