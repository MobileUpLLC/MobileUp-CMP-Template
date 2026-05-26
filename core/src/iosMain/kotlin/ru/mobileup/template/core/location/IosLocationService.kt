package ru.mobileup.template.core.location

import kotlinx.coroutines.delay

class IosLocationService : LocationService {

   override suspend fun getCurrentLocation(params: LocationRequestParams): LocationResult {
       // TODO: implement for iOS
       delay(500)
       return LocationResult.Failure(LocationError.CannotDetermineLocation)
   }
}
