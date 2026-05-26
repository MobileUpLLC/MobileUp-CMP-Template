package ru.mobileup.template.core.location

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Provides the device location using platform-specific location APIs.
 */
interface LocationService {

    /**
     * Returns the current device location or a typed location error.
     */
    suspend fun getCurrentLocation(
        params: LocationRequestParams = LocationRequestParams()
    ): LocationResult
}

/**
 * Defines how the service obtains a location for a single request.
 */
data class LocationRequestParams(
    /**
     * Maximum time spent trying to determine the current location.
     */
    val timeout: Duration = 10.seconds,

    /**
     * Desired accuracy level for the current location request.
     */
    val accuracy: LocationAccuracy = LocationAccuracy.High,

    /**
     * Defines whether the service can return the platform last known location
     * when the current location cannot be determined.
     */
    val fallbackLocationPolicy: FallbackLocationPolicy =
        FallbackLocationPolicy.LastKnown(maxAge = 30.seconds)
)

/**
 * Accuracy preference for a location request.
 */
enum class LocationAccuracy {

    /**
     * Prefer the most accurate location available.
     */
    High,

    /**
     * Prefer a balanced location mode with lower power usage.
     */
    Balanced
}

/**
 * Defines fallback behavior when the current location cannot be determined.
 */
sealed interface FallbackLocationPolicy {

    /**
     * Do not return the platform last known location.
     */
    data object Disabled : FallbackLocationPolicy

    /**
     * Return the platform last known location if it is not older than [maxAge].
     *
     * This fallback is used only when location permission is granted and
     * system location services are enabled.
     */
    data class LastKnown(
        val maxAge: Duration
    ) : FallbackLocationPolicy
}

/**
 * Result of a location request.
 */
sealed interface LocationResult {

    /**
     * Location was determined successfully.
     */
    data class Success(
        val location: GeoLocation
    ) : LocationResult

    /**
     * Location could not be determined.
     */
    data class Failure(
        val error: LocationError
    ) : LocationResult
}

/**
 * Domain-level location errors exposed to the application.
 */
sealed interface LocationError {

    /**
     * The application does not have location permission.
     */
    data object PermissionDenied : LocationError

    /**
     * Location services are disabled at the system level.
     */
    data object LocationServicesDisabled : LocationError

    /**
     * Location permission is granted and location services are enabled,
     * but the device location still could not be determined.
     */
    data object CannotDetermineLocation : LocationError
}

/**
 * Device location returned by the service.
 */
data class GeoLocation(
    /**
     * Geographic coordinate of the device.
     */
    val coordinate: GeoCoordinate,

    /**
     * Estimated horizontal accuracy in meters.
     *
     * Null means the platform did not provide a valid accuracy value.
     */
    val horizontalAccuracyMeters: Double?,

    /**
     * Moment when the location was measured by the platform.
     *
     * For last known locations this can be earlier than the request time.
     */
    val measuredAt: Instant?,

    /**
     * Source used to obtain this location.
     */
    val source: LocationSource
)

/**
 * Source of the returned location.
 */
enum class LocationSource {

    /**
     * Location was determined during the current request.
     */
    Current,

    /**
     * Platform last known location was used as a fallback.
     */
    LastKnown
}
