package ru.mobileup.template.core.error_handling

import ru.mobileup.template.core.generated.resources.Res
import ru.mobileup.template.core.generated.resources.error_deserialization
import ru.mobileup.template.core.generated.resources.error_matching_application_not_found
import ru.mobileup.template.core.generated.resources.error_no_internet_connection
import ru.mobileup.template.core.generated.resources.error_server
import ru.mobileup.template.core.generated.resources.error_server_unavailable
import ru.mobileup.template.core.generated.resources.error_ssl_handshake
import ru.mobileup.template.core.generated.resources.error_unauthorized
import ru.mobileup.template.core.generated.resources.error_unexpected
import ru.mobileup.template.core.utils.StringDesc
import ru.mobileup.template.core.utils.desc
import ru.mobileup.template.core.utils.plus
import ru.mobileup.template.core.utils.resourceDesc

/**
 * Returns human readable messages for exceptions.
 */
fun Exception.getErrorMessage(showDebugInfo: Boolean): StringDesc {
    return when (this) {

        is ExternalAppNotFoundException -> Res.string.error_matching_application_not_found.resourceDesc()

        is ServerUnavailableException -> Res.string.error_server_unavailable.resourceDesc()

        is NoInternetException -> Res.string.error_no_internet_connection.resourceDesc()

        is UnauthorizedException -> Res.string.error_unauthorized.resourceDesc()

        is SSLHandshakeException -> Res.string.error_ssl_handshake.resourceDesc()

        is ServerException -> message?.desc()
            ?: Res.string.error_server.resourceDesc()

        is DeserializationException -> {
            val description = this.message
            if (description != null && showDebugInfo) {
                Res.string.error_deserialization.resourceDesc() + "\n\n$description".desc()
            } else {
                Res.string.error_deserialization.resourceDesc()
            }
        }

        else -> {
            val description = this.message
            if (description != null && showDebugInfo) {
                Res.string.error_unexpected.resourceDesc() + "\n\n$description".desc()
            } else {
                Res.string.error_unexpected.resourceDesc()
            }
        }
    }
}
