import UIKit
import SwiftUI
import Shared
import YandexMapsMobile


@main
struct iOSApp: App {
    private let sharedApp: SharedApp

    init() {
        iOSApp.initializeMapKit()
        sharedApp = SharedApp(configuration: makeConfiguration())
    }

    var body: some Scene {
        WindowGroup {
            RootView(sharedApp: sharedApp)
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }

    private static func initializeMapKit() {
        guard let apiKey = Bundle.main.object(forInfoDictionaryKey: "YANDEX_MAP_API_KEY") as? String,
              !apiKey.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            fatalError(
                "YANDEX_MAP_API_KEY is not configured. "
                + "Copy iosApp/Configuration/Secrets.xcconfig.example to Secrets.xcconfig and set the key."
            )
        }

        YMKMapKit.setApiKey(apiKey)
        YMKMapKit.setLocale("ru_RU")
        _ = YMKMapKit.sharedInstance()
    }
}

private func makeConfiguration() -> Configuration {
    #if DEBUG
    let buildType = BuildType.debug
    #else
    let buildType = BuildType.release_
    #endif

    return Configuration(
        platform: Platform(iosMapControllerFactory: IosYandexMapControllerFactory()),
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
