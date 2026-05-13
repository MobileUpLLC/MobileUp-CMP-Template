package ru.mobileup.template.core.network

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.logging.Logger as KtorLogger

/**
 * Creates implementations of Ktorfit APIs.
 */
class NetworkApiFactory(
    private val backendUrl: BackendUrl,
    private val httpClientEngine: HttpClientEngine,
    private val ktorLogger: KtorLogger? = null
) {
    companion object {
        private const val CONNECT_TIMEOUT_MILLISECONDS = 30000L
        private const val READ_WRITE_TIMEOUT_MILLISECONDS = 60000L
    }

    private val json = createDefaultJson()
    private val authorizedHttpClient = createHttpClient(authorized = true)
    private val unauthorizedHttpClient = createHttpClient(authorized = false)

    /**
     * Ktorfit for creating of authorized APIs
     */
    val authorizedKtorfit = createKtorfit(authorizedHttpClient)

    /**
     * Ktorfit for creating of APIs that don't require authorization
     */
    val unauthorizedKtorfit = createKtorfit(unauthorizedHttpClient)

    private fun createHttpClient(authorized: Boolean): HttpClient {
        return HttpClient(httpClientEngine) {
            if (ktorLogger != null) {
                install(Logging) {
                    logger = ktorLogger
                    level = LogLevel.ALL
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLISECONDS
                requestTimeoutMillis = READ_WRITE_TIMEOUT_MILLISECONDS
            }

            defaultRequest {
                url(backendUrl.value)
            }

            if (authorized) {
                // TODO: add Auth interceptor
            }

            setupErrorConverter()
        }
    }

    private fun createKtorfit(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl(backendUrl.value)
            .httpClient(httpClient)
            .build()
    }
}
