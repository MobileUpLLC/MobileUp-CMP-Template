import YandexMapsMobile
import Shared

final class CameraController: NSObject, YMKMapCameraListener {

    private static let animationDurationSeconds: Float = 1.0

    private let mapView: YMKMapView
    private let onCameraPositionChange: (MapCameraPosition) -> Void

    init(
        mapView: YMKMapView,
        onCameraPositionChange: @escaping (MapCameraPosition) -> Void
    ) {
        self.mapView = mapView
        self.onCameraPositionChange = onCameraPositionChange
        super.init()
        mapView.mapWindow.map.addCameraListener(with: self)
    }

    var cameraPosition: MapCameraPosition {
        mapView.mapWindow.map.cameraPosition.mapCameraPosition
    }

    func moveCamera(position: MapCameraPosition, animate: Bool) {
        let map = mapView.mapWindow.map
        if animate {
            map.move(
                with: position.ymkCameraPosition,
                animation: YMKAnimation(type: .smooth, duration: Self.animationDurationSeconds)
            )
        } else {
            map.move(with: position.ymkCameraPosition)
        }
    }

    func calculateBoundingBoxCameraPosition(coordinates: [GeoCoordinate]) -> MapCameraPosition? {
        guard !coordinates.isEmpty else { return nil }

        let map = mapView.mapWindow.map
        let geometry = YMKGeometry(polyline: YMKPolyline(points: coordinates.map { $0.ymkPoint }))
        let currentPosition = map.cameraPosition
        let position = map.cameraPosition(
            with: geometry,
            focus: nil,
            azimuth: NSNumber(value: currentPosition.azimuth),
            tilt: NSNumber(value: currentPosition.tilt)
        )
        return position.mapCameraPosition
    }

    func dispose() {
        mapView.mapWindow.map.removeCameraListener(with: self)
    }

    // MARK: - YMKMapCameraListener

    func onCameraPositionChanged(
        with map: YMKMap,
        cameraPosition: YMKCameraPosition,
        cameraUpdateReason: YMKCameraUpdateReason,
        finished: Bool
    ) {
        onCameraPositionChange(cameraPosition.mapCameraPosition)
    }
}
