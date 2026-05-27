import UIKit
import Shared

final class IosDebugMapControllerFactory: NSObject, IosMapControllerFactory {

    func create(
        onCameraPositionChange: @escaping (MapCameraPosition) -> Void,
        onMarkerClick: @escaping (MapMarker) -> Void,
        onClusterClick: @escaping ([MapMarker]) -> Void
    ) -> IosMapController {
        IosDebugMapController(
            onCameraPositionChange: onCameraPositionChange,
            onMarkerClick: onMarkerClick,
            onClusterClick: onClusterClick
        )
    }
}

final class IosDebugMapController: NSObject, IosMapController {

    let view = UIView()

    private let titleLabel = UILabel()
    private let detailsLabel = UILabel()
    private let onCameraPositionChange: (MapCameraPosition) -> Void

    private var currentCameraPosition = MapCameraPosition(
        coordinate: GeoCoordinate(latitude: 55.752004, longitude: 37.617734),
        zoom: 6.0,
        azimuth: 0.0,
        tilt: 0.0
    )
    private var markers: [MapMarker] = []
    private var isCurrentLocationMarkerVisible = false
    private var theme: MapTheme = .default_
    private var logoPosition = "not set"
    private var lastAction = "created"
    private var isDisposed = false

    var cameraPosition: MapCameraPosition {
        currentCameraPosition
    }

    init(
        onCameraPositionChange: @escaping (MapCameraPosition) -> Void,
        onMarkerClick: @escaping (MapMarker) -> Void,
        onClusterClick: @escaping ([MapMarker]) -> Void
    ) {
        self.onCameraPositionChange = onCameraPositionChange

        super.init()

        _ = onMarkerClick
        _ = onClusterClick

        setupView()
        updateDebugText()
    }

    func moveCamera(position: MapCameraPosition, animate: Bool) {
        currentCameraPosition = position
        lastAction = "moveCamera(animate: \(animate))"
        updateDebugText()
        onCameraPositionChange(position)
    }

    func calculateBoundingBoxCameraPosition(coordinates: [GeoCoordinate]) -> MapCameraPosition? {
        guard !coordinates.isEmpty else {
            lastAction = "calculateBoundingBoxCameraPosition(empty)"
            updateDebugText()
            return nil
        }

        // TODO: неправильные формулы, нужно использовать метод из Yandex Map SDK для рассчетов
        let latitude = coordinates.map(\.latitude).reduce(0.0, +) / Double(coordinates.count)
        let longitude = coordinates.map(\.longitude).reduce(0.0, +) / Double(coordinates.count)
        lastAction = "calculateBoundingBoxCameraPosition(count: \(coordinates.count))"
        updateDebugText()

        return MapCameraPosition(
            coordinate: GeoCoordinate(latitude: latitude, longitude: longitude),
            zoom: currentCameraPosition.zoom,
            azimuth: currentCameraPosition.azimuth,
            tilt: currentCameraPosition.tilt
        )
    }

    func setMarkers(markers: [MapMarker]) {
        self.markers = markers
        lastAction = "setMarkers(count: \(markers.count))"
        updateDebugText()
    }

    func setCurrentLocationMarkerVisible(isVisible: Bool) {
        isCurrentLocationMarkerVisible = isVisible
        lastAction = "setCurrentLocationMarkerVisible(\(isVisible))"
        updateDebugText()
    }

    func setTheme(theme: MapTheme) {
        self.theme = theme
        lastAction = "setTheme(\(theme.name))"
        updateDebugText()
    }

    func setLogoPosition(position: MapLogoPosition) {
        logoPosition = "\(position.horizontalAlignment.name)/\(position.verticalAlignment.name), padding: \(position.horizontalPaddingPx)x\(position.verticalPaddingPx)"
        lastAction = "setLogoPosition"
        updateDebugText()
    }

    func dispose() {
        isDisposed = true
        lastAction = "dispose"
        updateDebugText()
    }

    private func setupView() {
        view.backgroundColor = UIColor.secondarySystemBackground

        titleLabel.text = "iOS Map Stub"
        titleLabel.font = .preferredFont(forTextStyle: .headline)
        titleLabel.textColor = .label
        titleLabel.textAlignment = .center

        detailsLabel.font = .monospacedSystemFont(ofSize: 13.0, weight: .regular)
        detailsLabel.textColor = .secondaryLabel
        detailsLabel.numberOfLines = 0
        detailsLabel.textAlignment = .center

        let stackView = UIStackView(arrangedSubviews: [titleLabel, detailsLabel])
        stackView.axis = .vertical
        stackView.alignment = .fill
        stackView.spacing = 12.0
        stackView.translatesAutoresizingMaskIntoConstraints = false

        view.addSubview(stackView)

        NSLayoutConstraint.activate([
            stackView.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            stackView.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            stackView.leadingAnchor.constraint(greaterThanOrEqualTo: view.leadingAnchor, constant: 16.0),
            stackView.trailingAnchor.constraint(lessThanOrEqualTo: view.trailingAnchor, constant: -16.0)
        ])
    }

    private func updateDebugText() {
        let coordinate = currentCameraPosition.coordinate
        detailsLabel.text = """
        camera: \(format(coordinate.latitude)), \(format(coordinate.longitude))
        zoom: \(format(Double(currentCameraPosition.zoom)))
        azimuth: \(format(Double(currentCameraPosition.azimuth)))
        tilt: \(format(Double(currentCameraPosition.tilt)))
        markers: \(markers.count)
        current location: \(isCurrentLocationMarkerVisible)
        theme: \(theme.name)
        logo: \(logoPosition)
        last action: \(lastAction)
        disposed: \(isDisposed)
        """
    }

    private func format(_ value: Double) -> String {
        String(format: "%.4f", value)
    }
}
