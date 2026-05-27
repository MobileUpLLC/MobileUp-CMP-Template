import UIKit
import SwiftUI
import Shared

struct RootView: UIViewControllerRepresentable {
    let sharedApp: SharedApp

    func makeUIViewController(context: Context) -> UIViewController {
        sharedApp.createRootViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
