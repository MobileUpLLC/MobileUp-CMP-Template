import UIKit
import SwiftUI
import Shared


@main
struct iOSApp: App {
    private let sharedApp = SharedApp(configuration: makeConfiguration())

    var body: some Scene {
        WindowGroup {
            RootView(sharedApp: sharedApp)
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}

private func makeConfiguration() -> Configuration {
    #if DEBUG
    let buildType = BuildType.debug
    let backend = Backend.development
    #else
    let buildType = BuildType.release_
    let backend = Backend.production
    #endif

    return Configuration(
        platform: Platform(),
        buildType: buildType,
        backend: backend
    )
}
