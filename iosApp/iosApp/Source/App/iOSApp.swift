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
    #else
    let buildType = BuildType.release_
    #endif

    return Configuration(
        platform: Platform(),
        buildType: buildType,
        backendUrl: getBackendUrl()
    )
}

private func getBackendUrl() -> String {
    guard let backendUrl = Bundle.main.object(forInfoDictionaryKey: "BACKEND_URL") as? String,
          !backendUrl.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
        fatalError("BACKEND_URL is not configured")
    }

    return backendUrl
}
