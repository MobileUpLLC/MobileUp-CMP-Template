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
