package ru.mobileup.template.core_testing.network

import io.ktor.http.HttpMethod

/**
 * Predicate used by [MockServer] to find a response for an incoming request.
 */
fun interface RequestMatcher {
    fun matches(request: HttpRequest): Boolean

    /**
     * Matches any request.
     */
    companion object : RequestMatcher {
        override fun matches(request: HttpRequest): Boolean = true

        override fun toString(): String = "any request"
    }
}

/**
 * Extends this matcher with a path contains check.
 */
fun RequestMatcher.containsPath(value: String): RequestMatcher {
    val baseMatcher = this
    return requestMatcher("$baseMatcher with path containing \"$value\"") { request ->
        baseMatcher.matches(request) && request.path.contains(value)
    }
}

/**
 * Extends this matcher with an exact path check.
 */
fun RequestMatcher.exactPath(value: String): RequestMatcher {
    val baseMatcher = this
    return requestMatcher("$baseMatcher with path \"$value\"") { request ->
        baseMatcher.matches(request) && request.path == value
    }
}

/**
 * Extends this matcher with an HTTP method check.
 */
fun RequestMatcher.method(value: HttpMethod): RequestMatcher {
    val baseMatcher = this
    return requestMatcher("$baseMatcher with method ${value.value}") { request ->
        baseMatcher.matches(request) && request.method == value
    }
}

private fun requestMatcher(
    description: String,
    matches: (HttpRequest) -> Boolean
): RequestMatcher {
    return object : RequestMatcher {
        override fun matches(request: HttpRequest): Boolean {
            return matches(request)
        }

        override fun toString(): String = description
    }
}
