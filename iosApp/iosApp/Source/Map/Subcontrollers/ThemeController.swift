import YandexMapsMobile
import Shared

final class ThemeController {

    private let mapView: YMKMapView

    init(mapView: YMKMapView) {
        self.mapView = mapView
    }

    func setTheme(theme: MapTheme) {
        mapView.mapWindow.map.setMapStyleWithStyle(theme.yandexMapStyleJson)
    }
}
