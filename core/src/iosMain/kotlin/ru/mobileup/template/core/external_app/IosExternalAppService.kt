package ru.mobileup.template.core.external_app

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import ru.mobileup.template.core.error_handling.ExternalAppNotFoundException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IosExternalAppService : ExternalAppService {

    override suspend fun openUrl(url: String) {
        val link = NSURL(string = url)
        openLink(link)
    }

    override suspend fun openAppSettings() {
        val link = NSURL(string = UIApplicationOpenSettingsURLString)
        openLink(link)
    }

    private suspend fun openLink(link: NSURL) {
        suspendCancellableCoroutine<Unit> { continuation ->
            UIApplication.sharedApplication.openURL(
                link,
                options = emptyMap<Any?, Any>(),
                completionHandler = { success ->
                    if (!continuation.isActive) return@openURL

                    if (success) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            ExternalAppNotFoundException(null)
                        )
                    }
                }
            )
        }
    }
}
