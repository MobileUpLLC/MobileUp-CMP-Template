import YandexMapsMobile
import Shared

final class LogoController {

    private let mapView: YMKMapView

    init(mapView: YMKMapView) {
        self.mapView = mapView
    }

    func setPosition(position: MapLogoPosition) {
        let logo = mapView.mapWindow.map.logo
        logo.setAlignmentWith(
            YMKLogoAlignment(
                horizontalAlignment: position.horizontalAlignment.ymkAlignment,
                verticalAlignment: position.verticalAlignment.ymkAlignment
            )
        )
        logo.setPaddingWith(
            YMKLogoPadding(
                horizontalPadding: UInt(max(0, position.horizontalPaddingPx)),
                verticalPadding: UInt(max(0, position.verticalPaddingPx))
            )
        )
    }
}

private extension MapLogoHorizontalAlignment {
    var ymkAlignment: YMKLogoHorizontalAlignment {
        // Kotlin enums bridge to objects, so compare by entry name rather than switch.
        self == MapLogoHorizontalAlignment.left ? .left : .right
    }
}

private extension MapLogoVerticalAlignment {
    var ymkAlignment: YMKLogoVerticalAlignment {
        self == MapLogoVerticalAlignment.top ? .top : .bottom
    }
}
