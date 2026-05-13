package ru.mobileup.template.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

fun createOkHttpEngine(): HttpClientEngine = Darwin.create()