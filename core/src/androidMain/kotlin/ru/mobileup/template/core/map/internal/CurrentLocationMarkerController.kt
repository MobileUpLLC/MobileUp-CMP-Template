package ru.mobileup.template.core.map.internal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import ru.mobileup.template.core.R

private val ACCURACY_CIRCLE_COLOR = Color(0x99B6B6B6).toArgb()
private const val DEFAULT_SCALE = 0.2f

internal class CurrentLocationMarkerController(
    private val mapView: MapView
) : UserLocationObjectListener {

    private var mapKit: MapKit = MapKitFactory.getInstance()
    private var userLocationLayer: UserLocationLayer? = mapKit.createUserLocationLayer(mapView.mapWindow)

    init {
        userLocationLayer?.setObjectListener(this)
    }

    fun setVisible(isVisible: Boolean) {
        if (isVisible) {
            mapKit.resetLocationManagerToDefault()
            userLocationLayer?.isVisible = true
        } else {
            userLocationLayer?.isVisible = false
        }
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        val context = mapView.context

        userLocationView.accuracyCircle.fillColor = ACCURACY_CIRCLE_COLOR

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(context, R.drawable.ic_my_location_arrow),
            IconStyle()
                .setRotationType(RotationType.ROTATE)
                .setScale(DEFAULT_SCALE)
        )

        userLocationView.pin.setIcon(
            ImageProvider.fromResource(context, R.drawable.ic_my_location_dot),
            IconStyle()
                .setScale(DEFAULT_SCALE)
        )
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) = Unit

    override fun onObjectUpdated(userLocationView: UserLocationView, event: ObjectEvent) = Unit
}
