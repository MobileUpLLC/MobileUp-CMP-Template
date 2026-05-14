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
    private val unmatchedRequests = mutableListOf<HttpRequest>()

    /**
     * Enqueues one-time response for requests matching [matcher].
     *
     * Each response is consumed by exactly one matching request.
     */
    suspend fun enqueue(matcher: RequestMatcher, response: HttpResponse) {
        mutex.withLock {
            rules.add(MockRule(matcher, response))
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

    /**
     * Fails if a test left unused responses or triggered requests without a matching response.
     */
    suspend fun verify() {
        val errorMessage = mutex.withLock {
            buildVerificationErrorMessage()
        }
        check(errorMessage == null) { errorMessage.orEmpty() }
    }

    internal suspend fun handleRequest(request: HttpRequest): HttpResponse {
        val response = mutex.withLock {
            recordedRequests.add(request)

            val ruleIndex = rules.indexOfFirst { it.matcher.matches(request) }
            if (ruleIndex < 0) {
                unmatchedRequests.add(request)
                error("No mock response enqueued for request: ${request.toDebugString()}")
            }

            rules.removeAt(ruleIndex).response
        }

        if (response.delay > Duration.ZERO) {
            // Works with virtual test time via kotlinx-coroutines-test.
            delay(response.delay)
        }

        return response
    }

    private fun buildVerificationErrorMessage(): String? {
        if (unmatchedRequests.isEmpty() && rules.isEmpty()) return null

        return buildString {
            appendLine("MockServer verification failed.")

            if (unmatchedRequests.isNotEmpty()) {
                appendLine("Unmatched requests:")
                unmatchedRequests.forEach { request ->
                    appendLine("- ${request.toDebugString()}")
                }
            }

            if (rules.isNotEmpty()) {
                appendLine("Unused mock responses:")
                rules.forEach { rule ->
                    appendLine("- ${rule.matcher}")
                }
            }
        }.trimEnd()
    }

    private fun HttpRequest.toDebugString(): String {
        return "${method.value} $fullUrl"
    }

    private data class MockRule(
        val matcher: RequestMatcher,
        val response: HttpResponse
    )
}
