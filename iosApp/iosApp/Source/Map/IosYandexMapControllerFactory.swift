import Shared

final class IosYandexMapControllerFactory: NSObject, IosMapControllerFactory {

    func create(
        onCameraPositionChange: @escaping (MapCameraPosition) -> Void,
        onMarkerClick: @escaping (MapMarker) -> Void,
        onClusterClick: @escaping ([MapMarker]) -> Void
    ) -> IosMapController {
        IosYandexMapController(
            onCameraPositionChange: onCameraPositionChange,
            onMarkerClick: onMarkerClick,
            onClusterClick: onClusterClick
        )
    }
}
