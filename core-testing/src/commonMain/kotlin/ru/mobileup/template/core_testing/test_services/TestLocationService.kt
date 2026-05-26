package ru.mobileup.template.core_testing.test_services

import ru.mobileup.template.core.location.GeoCoordinate
import ru.mobileup.template.core.location.GeoLocation
import ru.mobileup.template.core.location.LocationError
import ru.mobileup.template.core.location.LocationRequestParams
import ru.mobileup.template.core.location.LocationResult
import ru.mobileup.template.core.location.LocationService
import ru.mobileup.template.core.location.LocationSource

class TestLocationService : LocationService {

    private var result: LocationResult? = null

    fun setSuccessLocationResult(coordinate: GeoCoordinate) {
        result = LocationResult.Success(
            location = GeoLocation(
                coordinate = coordinate,
                horizontalAccuracyMeters = null,
                measuredAt = null,
                source = LocationSource.Current
            )
        )
    }

    fun setFailureLocationResult(error: LocationError) {
        result = LocationResult.Failure(error)
    }

    override suspend fun getCurrentLocation(params: LocationRequestParams): LocationResult {
        return result ?: error("TestLocationService result is not configured")
    }
}
