package ru.mobileup.template.core.map.internal.subcontrollers

import android.view.LayoutInflater
import android.widget.TextView
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterTapListener
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import ru.mobileup.template.core.R
import ru.mobileup.template.core.map.MapMarker

private const val CLUSTER_RADIUS = 40.0
private const val CLUSTER_MIN_ZOOM = 12
private const val PLACE_PIN_SCALE = 0.2f

internal class ClusterizedMarkersController(
    private val mapView: MapView,
    private val onMarkerClick: (MapMarker) -> Unit,
    private val onClusterClick: (List<MapMarker>) -> Unit
) {

    private var clusterCollection: ClusterizedPlacemarkCollection? = null

    private val markerTapListener = MapObjectTapListener { mapObject, _ ->
        val marker = mapObject.userData as? MapMarker ?: return@MapObjectTapListener false
        onMarkerClick(marker)
        true
    }

    private val clusterTapListener = ClusterTapListener { cluster ->
        val markers = cluster.placemarks.mapNotNull { it.userData as? MapMarker }
        onClusterClick(markers)
        true
    }

    private val clusterListener = ClusterListener { cluster ->
        cluster.appearance.setView(
            ViewProvider(
                LayoutInflater.from(mapView.context).inflate(
                    R.layout.yandex_map_cluster_view, null, false
                ).apply {
                    val clusterSize = findViewById<TextView>(R.id.clusterSizeTv)
                    clusterSize.text = cluster.size.toString()
                }
            )
        )
        cluster.addClusterTapListener(clusterTapListener)
    }

    fun setMarkers(markers: List<MapMarker>) {
        val map = mapView.mapWindow.map
        map.mapObjects.clear()
        clusterCollection = map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)
        markers.forEach { addMarker(it) }
        clusterCollection?.clusterPlacemarks(CLUSTER_RADIUS, CLUSTER_MIN_ZOOM)
        clusterCollection?.isVisible = true
    }

    private fun addMarker(marker: MapMarker) {
        clusterCollection?.addPlacemark()?.apply {
            userData = marker
            geometry = marker.coordinate.toPoint()
            addTapListener(markerTapListener)
            setIcon(
                ImageProvider.fromResource(
                    mapView.context,
                    R.drawable.ic_map_pin,
                    true
                ),
                IconStyle().apply { scale = PLACE_PIN_SCALE }
            )
        }
    }
}
