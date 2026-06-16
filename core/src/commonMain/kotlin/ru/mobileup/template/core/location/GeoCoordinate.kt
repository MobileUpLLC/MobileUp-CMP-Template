package ru.mobileup.template.core.location

import kotlinx.serialization.Serializable

@Serializable
data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        val KREMLIN = GeoCoordinate(55.752004, 37.617734)
    }
}
