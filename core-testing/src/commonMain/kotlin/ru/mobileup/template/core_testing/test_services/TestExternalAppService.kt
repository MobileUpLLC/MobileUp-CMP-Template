package ru.mobileup.template.core_testing.test_services

import ru.mobileup.template.core.external_app.ExternalAppService

/**
 * Test implementation of [ru.mobileup.template.core.external_app.ExternalAppService].
 *
 * Stores interactions for assertions.
 */
class TestExternalAppService : ExternalAppService {

    private val _interactions = mutableListOf<Interaction>()

    val interactions: List<Interaction> get() = _interactions
    val lastInteraction: Interaction? get() = _interactions.lastOrNull()
    val firstInteraction: Interaction? get() = _interactions.firstOrNull()
    val wasNoInteractions: Boolean get() = _interactions.isEmpty()

    override suspend fun openUrl(url: String) {
        _interactions += Interaction.OpenUrl(url)
    }

    override suspend fun openAppSettings() {
        _interactions += Interaction.OpenAppSettings
    }

    override suspend fun openLocationSettings() {
        _interactions += Interaction.OpenLocationSettings
    }

    sealed class Interaction {
        data class OpenUrl(val url: String) : Interaction()
        data object OpenAppSettings : Interaction()
        data object OpenLocationSettings : Interaction()
    }
}
