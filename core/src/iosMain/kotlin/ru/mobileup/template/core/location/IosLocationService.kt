@file:OptIn(ExperimentalForeignApi::class)

package ru.mobileup.template.core.location

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import ru.mobileup.template.core.utils.e
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Instant

class IosLocationService : LocationService {

    private val locationManager = CLLocationManager()

    override suspend fun getCurrentLocation(params: LocationRequestParams): LocationResult {

        if (!hasLocationPermission()) {
            return LocationResult.Failure(LocationError.PermissionDenied)
        }

        if (!isLocationServicesEnabled()) {
            return LocationResult.Failure(LocationError.LocationServicesDisabled)
        }

        return try {
            val currentLocation = getCurrentLocationOrNull(params)

            if (currentLocation != null) {
                return currentLocation.toLocationResult(LocationSource.Current)
            }

            val lastKnownLocation = getLastKnownLocationOrNull(params.fallbackLocationPolicy)

            if (lastKnownLocation != null) {
                lastKnownLocation.toLocationResult(LocationSource.LastKnown)
            } else {
                LocationResult.Failure(LocationError.CannotDetermineLocation)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e)
            LocationResult.Failure(LocationError.CannotDetermineLocation)
        }
    }

    private suspend fun getCurrentLocationOrNull(params: LocationRequestParams): CLLocation? {
        return try {
            withTimeoutOrNull(params.timeout) {
                requestSingleLocation(params.accuracy)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e)
            null
        }
    }

    private suspend fun requestSingleLocation(accuracy: LocationAccuracy): CLLocation? =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                    override fun locationManager(
                        manager: CLLocationManager,
                        didUpdateLocations: List<*>
                    ) {
                        if (!continuation.isActive) return
                        continuation.resume(didUpdateLocations.lastOrNull() as? CLLocation)
                    }

                    override fun locationManager(
                        manager: CLLocationManager,
                        didFailWithError: NSError
                    ) {
                        if (!continuation.isActive) return
                        Logger.e { "Location request failed: ${didFailWithError.localizedDescription}" }
                        continuation.resume(null)
                    }
                }

                continuation.invokeOnCancellation {
                    dispatch_async(dispatch_get_main_queue()) {
                        locationManager.stopUpdatingLocation()
                        locationManager.delegate = null
                    }
                }

                locationManager.delegate = delegate
                locationManager.desiredAccuracy = accuracy.toCLAccuracy()
                locationManager.requestLocation()
            }
        }

    private suspend fun getLastKnownLocationOrNull(policy: FallbackLocationPolicy): CLLocation? {
        return when (policy) {
            FallbackLocationPolicy.Disabled -> null

            is FallbackLocationPolicy.LastKnown -> {
                withContext(Dispatchers.Main) { locationManager.location }
                    ?.takeIf { location -> location.isNotOlderThan(policy.maxAge) }
            }
        }
    }

    private suspend fun hasLocationPermission(): Boolean {
        val status = withContext(Dispatchers.Main) { locationManager.authorizationStatus }
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways
    }

    private suspend fun isLocationServicesEnabled(): Boolean {
        return withContext(Dispatchers.Default) { CLLocationManager.locationServicesEnabled() }
    }

    private fun LocationAccuracy.toCLAccuracy(): Double {
        return when (this) {
            LocationAccuracy.High -> kCLLocationAccuracyBest
            LocationAccuracy.Balanced -> kCLLocationAccuracyHundredMeters
        }
    }

    private fun CLLocation.isNotOlderThan(maxAge: Duration): Boolean {
        val ageMillis = (NSDate().timeIntervalSince1970 - timestamp.timeIntervalSince1970) * 1000
        return ageMillis in 0.0..maxAge.inWholeMilliseconds.toDouble()
    }

    private fun CLLocation.toLocationResult(source: LocationSource): LocationResult.Success {
        val (latitude, longitude) = coordinate.useContents { latitude to longitude }
        return LocationResult.Success(
            location = GeoLocation(
                coordinate = GeoCoordinate(
                    latitude = latitude,
                    longitude = longitude
                ),
                horizontalAccuracyMeters = horizontalAccuracy.takeIf { accuracy -> accuracy >= 0 },
                measuredAt = Instant.fromEpochMilliseconds(
                    (timestamp.timeIntervalSince1970 * 1000).toLong()
                ),
                source = source
            )
        )
    }
}
