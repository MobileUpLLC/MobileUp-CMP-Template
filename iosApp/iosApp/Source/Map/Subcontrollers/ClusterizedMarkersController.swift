import YandexMapsMobile
import Shared

final class ClusterizedMarkersController: NSObject, YMKClusterListener, YMKClusterTapListener, YMKMapObjectTapListener {

    private static let clusterRadius: Double = 40.0
    private static let clusterMinZoom: UInt = 12
    private static let placePinScale: NSNumber = 0.6

    private let mapView: YMKMapView
    private let onMarkerClick: (MapMarker) -> Void
    private let onClusterClick: ([MapMarker]) -> Void

    private var clusterCollection: YMKClusterizedPlacemarkCollection?

    init(
        mapView: YMKMapView,
        onMarkerClick: @escaping (MapMarker) -> Void,
        onClusterClick: @escaping ([MapMarker]) -> Void
    ) {
        self.mapView = mapView
        self.onMarkerClick = onMarkerClick
        self.onClusterClick = onClusterClick
        super.init()
    }

    func setMarkers(markers: [MapMarker]) {
        let map = mapView.mapWindow.map
        map.mapObjects.clear()
        let collection = map.mapObjects.addClusterizedPlacemarkCollection(with: self)
        clusterCollection = collection
        markers.forEach { addMarker($0, to: collection) }
        collection.clusterPlacemarks(withClusterRadius: Self.clusterRadius, minZoom: Self.clusterMinZoom)
        collection.isVisible = true
    }

    func dispose() {
        mapView.mapWindow.map.mapObjects.clear()
        clusterCollection = nil
    }

    private func addMarker(_ marker: MapMarker, to collection: YMKClusterizedPlacemarkCollection) {
        let placemark = collection.addPlacemark()
        placemark.userData = marker
        placemark.geometry = marker.coordinate.ymkPoint
        placemark.addTapListener(with: self)

        if let pinImage = UIImage(named: "ic_map_pin") {
            let style = YMKIconStyle()
            style.scale = Self.placePinScale
            placemark.setIconWith(pinImage, style: style)
        }
    }

    // MARK: - YMKMapObjectTapListener

    func onMapObjectTap(with mapObject: YMKMapObject, point: YMKPoint) -> Bool {
        guard let marker = mapObject.userData as? MapMarker else { return false }
        onMarkerClick(marker)
        return true
    }

    // MARK: - YMKClusterListener

    func onClusterAdded(with cluster: YMKCluster) {
        cluster.appearance.setIconWith(ClusterIconRenderer.image(count: cluster.size))
        cluster.addClusterTapListener(with: self)
    }

    // MARK: - YMKClusterTapListener

    func onClusterTap(with cluster: YMKCluster) -> Bool {
        let markers = cluster.placemarks.compactMap { $0.userData as? MapMarker }
        onClusterClick(markers)
        return true
    }
}
