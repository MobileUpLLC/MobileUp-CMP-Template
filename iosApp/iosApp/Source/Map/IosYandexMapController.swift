import YandexMapsMobile
import Shared

final class IosYandexMapController: NSObject, IosMapController {

    let view: UIView

    private let mapView: YMKMapView

    private let cameraController: CameraController
    private let clusterizedMarkersController: ClusterizedMarkersController
    private let currentLocationMarkerController: CurrentLocationMarkerController
    private let themeController: ThemeController
    private let logoController: LogoController

    init(
        onCameraPositionChange: @escaping (MapCameraPosition) -> Void,
        onMarkerClick: @escaping (MapMarker) -> Void,
        onClusterClick: @escaping ([MapMarker]) -> Void
    ) {
        let mapView = YMKMapView(frame: .zero)!
        self.mapView = mapView
        self.view = mapView

        cameraController = CameraController(
            mapView: mapView,
            onCameraPositionChange: onCameraPositionChange
        )
        clusterizedMarkersController = ClusterizedMarkersController(
            mapView: mapView,
            onMarkerClick: onMarkerClick,
            onClusterClick: onClusterClick
        )
        currentLocationMarkerController = CurrentLocationMarkerController(mapView: mapView)
        themeController = ThemeController(mapView: mapView)
        logoController = LogoController(mapView: mapView)

        super.init()
    }

    var cameraPosition: MapCameraPosition {
        cameraController.cameraPosition
    }

    func moveCamera(position: MapCameraPosition, animate: Bool) {
        cameraController.moveCamera(position: position, animate: animate)
    }

    func calculateBoundingBoxCameraPosition(coordinates: [GeoCoordinate]) -> MapCameraPosition? {
        cameraController.calculateBoundingBoxCameraPosition(coordinates: coordinates)
    }

    func setMarkers(markers: [MapMarker]) {
        clusterizedMarkersController.setMarkers(markers: markers)
    }

    func setCurrentLocationMarkerVisible(isVisible: Bool) {
        currentLocationMarkerController.setVisible(isVisible: isVisible)
    }

    func setTheme(theme: MapTheme) {
        themeController.setTheme(theme: theme)
    }

    func setLogoPosition(position: MapLogoPosition) {
        logoController.setPosition(position: position)
    }

    func dispose() {
        cameraController.dispose()
        clusterizedMarkersController.dispose()
        currentLocationMarkerController.dispose()
    }
}
