package ru.mobileup.template.core.error_handling

/**
 * ApplicationException is a base class for application specific exceptions.
 * All exceptions occurred on a data are mapped to the application exceptions.
 */
abstract class ApplicationException(cause: Throwable?) : Exception(cause)

/**
 * No access to data (access token invalid or expired)
 */
class UnauthorizedException(cause: Throwable?) : ApplicationException(cause)

/**
 * Received a response from the server, but it is invalid - 4xx, 5xx
 */
class ServerException(cause: Throwable? = null, override val message: String? = null) :
    ApplicationException(cause)

/**
 * Failed to connect to the server and the problem is most likely on the client
 */
class NoInternetException(cause: Throwable?) : ApplicationException(cause)

/**
 * Failed to connect to the server and the problem is most likely on the server
 */
class ServerUnavailableException(cause: Throwable?) : ApplicationException(cause)

/**
 *  Problems parsing json or lack of fields
 */
class DeserializationException(cause: Throwable?) : ApplicationException(cause)

/**
 * Indicated that the client and server cannot agree on the desired level of security.
 * The problem may be on the server - the certificate has expired.
 * The problem may be on the client - verification of the date and time is required.
 */
class SSLHandshakeException(cause: Throwable?) : ApplicationException(cause)

/**
 * Could not find app for action
 */
class ExternalAppNotFoundException(cause: Throwable?) : ApplicationException(cause)

/**
 * Some unknown issue
 */
class UnknownException(cause: Throwable?, override val message: String) :
    ApplicationException(cause)