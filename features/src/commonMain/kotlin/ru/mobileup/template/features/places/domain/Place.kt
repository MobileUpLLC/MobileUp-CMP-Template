package ru.mobileup.template.features.places.domain

import kotlinx.serialization.Serializable
import ru.mobileup.template.core.location.GeoCoordinate
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class PlaceId(val value: String)

@Serializable
data class Place(
    val id: PlaceId,
    val name: String,
    val coordinate: GeoCoordinate
) {
    companion object {
        private val MOSCOW_MOCKS = listOf(
            Place(PlaceId("moscow-1"), "Moscow place 1", GeoCoordinate(55.741004, 37.587734)),
            Place(PlaceId("moscow-2"), "Moscow place 2", GeoCoordinate(55.772004, 37.617734)),
            Place(PlaceId("moscow-3"), "Moscow place 3", GeoCoordinate(55.723004, 37.557734)),
            Place(PlaceId("moscow-4"), "Moscow place 4", GeoCoordinate(55.734004, 37.617734)),
            Place(PlaceId("moscow-5"), "Moscow place 5", GeoCoordinate(55.745004, 37.607734)),
        )

        private val PERM_MOCKS = listOf(
            Place(PlaceId("perm-1"), "Perm place 1", GeoCoordinate(58.012762, 56.232483)),
            Place(PlaceId("perm-2"), "Perm place 2", GeoCoordinate(58.010762, 56.234483)),
            Place(PlaceId("perm-3"), "Perm place 3", GeoCoordinate(58.014762, 56.232483)),
            Place(PlaceId("perm-4"), "Perm place 4", GeoCoordinate(58.010762, 56.235483)),
            Place(PlaceId("perm-5"), "Perm place 5", GeoCoordinate(58.019762, 56.232483)),
        )

        private val SPB_MOCKS = listOf(
            Place(PlaceId("spb-1"), "SPB place 1", GeoCoordinate(59.933887, 30.324289)),
            Place(PlaceId("spb-2"), "SPB place 2", GeoCoordinate(59.934887, 30.324289)),
            Place(PlaceId("spb-3"), "SPB place 3", GeoCoordinate(59.933887, 30.325289)),
            Place(PlaceId("spb-4"), "SPB place 4", GeoCoordinate(59.935887, 30.324289)),
            Place(PlaceId("spb-5"), "SPB place 5", GeoCoordinate(59.933887, 30.326289)),
            Place(PlaceId("spb-6"), "SPB place 6", GeoCoordinate(59.936887, 30.324289)),
        )

        val MOCKS = listOf(MOSCOW_MOCKS, PERM_MOCKS, SPB_MOCKS).flatten()
    }
}

fun List<Place>.filterInMoscowRegion(): List<Place> {
    val latitudeRange = GeoCoordinate.KREMLIN.latitude - 1..GeoCoordinate.KREMLIN.latitude + 1
    val longitudeRange = GeoCoordinate.KREMLIN.longitude - 1..GeoCoordinate.KREMLIN.longitude + 1

    return filter { place ->
        place.coordinate.latitude in latitudeRange &&
            place.coordinate.longitude in longitudeRange
    }
}
