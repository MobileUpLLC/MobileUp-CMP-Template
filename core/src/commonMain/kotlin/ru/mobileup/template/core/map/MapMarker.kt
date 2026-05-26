package ru.mobileup.template.core.map

import ru.mobileup.template.core.location.GeoCoordinate

data class MapMarker(
    val id: String,
    val coordinate: GeoCoordinate
)
