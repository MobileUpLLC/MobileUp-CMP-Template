package ru.mobileup.template.core.network

import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.MessageLengthLimitingLogger
import co.touchlab.kermit.Logger as KermitLogger

fun createKtorLogger(): Logger {
    val kermitLogger = KermitLogger.withTag("HttpClient")
    return MessageLengthLimitingLogger(
        delegate = object : Logger {
            override fun log(message: String) {
                kermitLogger.i(message)
            }
        }
    )
}