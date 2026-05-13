package ru.mobileup.template.core_testing.external_app

import ru.mobileup.template.core.external_app.ExternalAppService

class TestExternalAppService : ExternalAppService {

    override fun openUrl(url: String) = Unit

    override fun openAppSettings() = Unit
}
