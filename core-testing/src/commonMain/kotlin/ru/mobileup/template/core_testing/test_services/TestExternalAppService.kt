package ru.mobileup.template.core_testing.test_services

import ru.mobileup.template.core.external_app.ExternalAppService

/**
 * Test implementation of [ru.mobileup.template.core.external_app.ExternalAppService].
 *
 * Stores interactions for assertions.
 */
class TestExternalAppService : ExternalAppService {

    private val _all = mutableListOf<Interaction>()

    val all: List<Interaction> get() = _all
    val last: Interaction? get() = _all.lastOrNull()
    val first: Interaction? get() = _all.firstOrNull()
    val isEmpty: Boolean get() = _all.isEmpty()

    override suspend fun openUrl(url: String) {
        _all += Interaction.OpenUrl(url)
    }

    override suspend fun openAppSettings() {
        _all += Interaction.OpenAppSettings
    }

    override suspend fun openLocationSettings() {
        _all += Interaction.OpenLocationSettings
    }

    sealed class Interaction {
        data class OpenUrl(val url: String) : Interaction()
        data object OpenAppSettings : Interaction()
        data object OpenLocationSettings : Interaction()
    }
}
