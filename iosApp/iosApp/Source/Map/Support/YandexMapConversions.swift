import YandexMapsMobile
import Shared

extension GeoCoordinate {
    var ymkPoint: YMKPoint {
        YMKPoint(latitude: latitude, longitude: longitude)
    }
}

extension YMKPoint {
    var geoCoordinate: GeoCoordinate {
        GeoCoordinate(latitude: latitude, longitude: longitude)
    }
}

extension MapCameraPosition {
    var ymkCameraPosition: YMKCameraPosition {
        YMKCameraPosition(
            target: coordinate.ymkPoint,
            zoom: zoom,
            azimuth: azimuth,
            tilt: tilt
        )
    }
}

extension YMKCameraPosition {
    var mapCameraPosition: MapCameraPosition {
        MapCameraPosition(
            coordinate: target.geoCoordinate,
            zoom: zoom,
            azimuth: azimuth,
            tilt: tilt
        )
    }
}
