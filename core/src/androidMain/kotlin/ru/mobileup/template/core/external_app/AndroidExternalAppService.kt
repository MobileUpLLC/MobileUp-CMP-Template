package ru.mobileup.template.core.external_app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri
import ru.mobileup.template.core.error_handling.ExternalAppNotFoundException

class AndroidExternalAppService(
    private val context: Context
) : ExternalAppService {

    override fun openUrl(url: String) {
        val intent = if (url.startsWith("intent:")) {
            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        } else {
            Intent(Intent.ACTION_VIEW, url.toUri())
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safeStartActivity(intent)
    }

    override fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safeStartActivity(intent)
    }

    private fun safeStartActivity(intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            throw ExternalAppNotFoundException(e)
        }
    }
}
