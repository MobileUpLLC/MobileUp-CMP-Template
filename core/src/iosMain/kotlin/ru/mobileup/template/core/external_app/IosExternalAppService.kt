package ru.mobileup.template.core.external_app

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import ru.mobileup.template.core.error_handling.ExternalAppNotFoundException

class IosExternalAppService : ExternalAppService {

    override fun openUrl(url: String) {
        val link = NSURL(string = url)
        openLinkIfAvailable(link)
    }

    override fun openAppSettings() {
        val link = NSURL(string = UIApplicationOpenSettingsURLString)
        openLinkIfAvailable(link)
    }

    private fun openLinkIfAvailable(link: NSURL) {
        if (UIApplication.sharedApplication.canOpenURL(link)) {
            UIApplication.sharedApplication.openURL(link)
        } else {
            throw ExternalAppNotFoundException(null)
        }
    }
}
