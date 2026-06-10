import YandexMapsMobile
import Shared

final class CurrentLocationMarkerController: NSObject, YMKUserLocationObjectListener {

    // 0x99B6B6B6 (semi-transparent gray), matching the Android accuracy circle color.
    private static let accuracyCircleColor = UIColor(
        red: 0xB6 / 255,
        green: 0xB6 / 255,
        blue: 0xB6 / 255,
        alpha: 0x99 / 255
    )
    private static let defaultScale: NSNumber = 0.2

    private let mapView: YMKMapView
    private let mapKit: YMKMapKit
    private var userLocationLayer: YMKUserLocationLayer?

    init(mapView: YMKMapView) {
        self.mapView = mapView
        self.mapKit = YMKMapKit.sharedInstance()
        super.init()

        let layer = mapKit.createUserLocationLayer(with: mapView.mapWindow)
        layer.setObjectListenerWith(self)
        userLocationLayer = layer
    }

    func setVisible(isVisible: Bool) {
        if isVisible {
            mapKit.resetLocationManagerToDefault()
            userLocationLayer?.setVisibleWithOn(true)
        } else {
            userLocationLayer?.setVisibleWithOn(false)
        }
    }

    func dispose() {
        userLocationLayer?.setVisibleWithOn(false)
        userLocationLayer?.setObjectListenerWith(nil)
        userLocationLayer = nil
    }

    // MARK: - YMKUserLocationObjectListener

    func onObjectAdded(with view: YMKUserLocationView) {
        view.accuracyCircle.fillColor = Self.accuracyCircleColor

        let arrowStyle = YMKIconStyle()
        arrowStyle.rotationType = NSNumber(value: YMKRotationType.rotate.rawValue)
        arrowStyle.scale = Self.defaultScale
        if let arrowImage = UIImage(named: "ic_my_location_arrow") {
            view.arrow.setIconWith(arrowImage, style: arrowStyle)
        }

        let pinStyle = YMKIconStyle()
        pinStyle.scale = Self.defaultScale
        if let pinImage = UIImage(named: "ic_my_location_dot") {
            view.pin.setIconWith(pinImage, style: pinStyle)
        }
    }

    func onObjectRemoved(with view: YMKUserLocationView) {}

    func onObjectUpdated(with view: YMKUserLocationView, event: YMKObjectEvent) {}
}
