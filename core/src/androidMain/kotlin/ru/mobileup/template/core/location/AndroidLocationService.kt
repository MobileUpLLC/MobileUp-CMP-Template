package ru.mobileup.template.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Instant

class AndroidLocationService(
    private val applicationContext: Context
) : LocationService {

    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)

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
        } catch (_: SecurityException) {
            LocationResult.Failure(LocationError.PermissionDenied)
        } catch (_: Exception) {
            LocationResult.Failure(LocationError.CannotDetermineLocation)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocationOrNull(params: LocationRequestParams): Location? {
        val cancellationTokenSource = CancellationTokenSource()
        return try {
            withTimeoutOrNull(params.timeout) {
                client
                    .getCurrentLocation(
                        params.accuracy.toAndroidPriority(),
                        cancellationTokenSource.token
                    )
                    .await(cancellationTokenSource)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SecurityException) {
            throw e
        } catch (_: Exception) {
            null
        } finally {
            cancellationTokenSource.cancel()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocationOrNull(policy: FallbackLocationPolicy): Location? {
        return when (policy) {
            FallbackLocationPolicy.Disabled -> null

            is FallbackLocationPolicy.LastKnown -> {
                client.lastLocation.await()
                    ?.takeIf { location -> location.isNotOlderThan(policy.maxAge) }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationServicesEnabled(): Boolean {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun LocationAccuracy.toAndroidPriority(): Int {
        return when (this) {
            LocationAccuracy.High -> PRIORITY_HIGH_ACCURACY
            LocationAccuracy.Balanced -> PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private fun Location.isNotOlderThan(maxAge: Duration): Boolean {
        val ageMillis = System.currentTimeMillis() - time
        return ageMillis in 0..maxAge.inWholeMilliseconds
    }

    private fun Location.toLocationResult(source: LocationSource): LocationResult.Success {
        return LocationResult.Success(
            location = GeoLocation(
                coordinate = GeoCoordinate(
                    latitude = latitude,
                    longitude = longitude
                ),
                horizontalAccuracyMeters = if (hasAccuracy()) {
                    accuracy.toDouble()
                } else {
                    null
                },
                measuredAt = Instant.fromEpochMilliseconds(time),
                source = source
            )
        )
    }
}
