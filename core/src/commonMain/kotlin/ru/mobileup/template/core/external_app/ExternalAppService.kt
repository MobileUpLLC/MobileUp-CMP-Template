package ru.mobileup.template.core.external_app

interface ExternalAppService {

    suspend fun openUrl(url: String)

    suspend fun openAppSettings()

    suspend fun openLocationSettings()
}
