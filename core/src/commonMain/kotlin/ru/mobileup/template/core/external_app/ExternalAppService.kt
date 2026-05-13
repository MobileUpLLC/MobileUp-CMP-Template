package ru.mobileup.template.core.external_app

import ru.mobileup.template.core.error_handling.ExternalAppNotFoundException

interface ExternalAppService {

    @Throws(ExternalAppNotFoundException::class)
    fun openUrl(url: String)

    @Throws(ExternalAppNotFoundException::class)
    fun openAppSettings()
}
