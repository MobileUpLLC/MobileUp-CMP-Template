package ru.mobileup.template.core_testing.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

/**
 * In-memory mock server used by integration tests.
 *
 * Responses are queued as one-time rules and consumed in request order.
 */
class MockServer {
    private val mutex = Mutex()
    private val rules = mutableListOf<MockRule>()
    private val recordedRequests = mutableListOf<HttpRequest>()

    /**
     * Enqueues one-time responses for requests matching [matcher].
     *
     * Each response is consumed by exactly one matching request.
     */
    suspend fun enqueue(matcher: RequestMatcher, vararg responses: HttpResponse) {
        mutex.withLock {
            responses.forEach { response ->
                rules.add(MockRule(matcher, response))
            }
        }
    }

    /**
     * Returns all recorded requests filtered by [matcher].
     *
     * Prefer asserting user-visible state/output in tests.
     * Use this only when the request itself is the observable behavior
     * (for example, create/update/delete command scenarios).
     */
    suspend fun getRecordedRequests(matcher: RequestMatcher = RequestMatcher): List<HttpRequest> {
        return mutex.withLock {
            recordedRequests.filter { matcher.matches(it) }
        }
    }

    internal suspend fun handleRequest(request: HttpRequest): HttpResponse {
        val response = mutex.withLock {
            recordedRequests.add(request)

            val ruleIndex = rules.indexOfFirst { it.matcher.matches(request) }
            check(ruleIndex >= 0) {
                "No mock response enqueued for request: ${request.method.value} ${request.fullUrl}"
            }

            rules.removeAt(ruleIndex).response
        }

        if (response.delay > Duration.ZERO) {
            // Works with virtual test time via kotlinx-coroutines-test.
            delay(response.delay)
        }

        return response
    }

    private data class MockRule(
        val matcher: RequestMatcher,
        val response: HttpResponse
    )
}
