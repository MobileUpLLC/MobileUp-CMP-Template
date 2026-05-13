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
    }
}

/**
 * Extends this matcher with a path contains check.
 */
fun RequestMatcher.containsPath(value: String) = RequestMatcher { request ->
    this.matches(request) && request.path.contains(value)
}

/**
 * Extends this matcher with an exact path check.
 */
fun RequestMatcher.exactPath(value: String) = RequestMatcher { request ->
    this.matches(request) && request.path == value
}

/**
 * Extends this matcher with an HTTP method check.
 */
fun RequestMatcher.method(value: HttpMethod) = RequestMatcher { request ->
    this.matches(request) && request.method == value
}
