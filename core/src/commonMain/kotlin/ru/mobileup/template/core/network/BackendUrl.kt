package ru.mobileup.template.core.network

import ru.mobileup.template.core.configuration.Backend
import kotlin.jvm.JvmInline

@JvmInline
value class BackendUrl(val value: String) {

    companion object {
        private val MainDevelopmentUrl = BackendUrl("https://pokeapi.co/")
        private val MainProductionUrl = BackendUrl("https://pokeapi.co/")

        fun getMainUrl(backend: Backend) = when (backend) {
            Backend.Development -> MainDevelopmentUrl
            Backend.Production -> MainProductionUrl
        }
    }
}