import UIKit

/// Renders the cluster icon (orange circle with the cluster size in the center).
/// Mirrors the Android `bg_map_cluster` drawable + `yandex_map_cluster_view` layout.
enum ClusterIconRenderer {

    private static let size: CGFloat = 32
    private static let strokeWidth: CGFloat = 2
    private static let fillColor = UIColor(red: 0xF3 / 255, green: 0xA7 / 255, blue: 0x23 / 255, alpha: 1)
    private static let strokeColor = UIColor(red: 0x9B / 255, green: 0x9B / 255, blue: 0x9B / 255, alpha: 1)

    static func image(count: UInt) -> UIImage {
        let canvasSize = CGSize(width: size, height: size)
        let renderer = UIGraphicsImageRenderer(size: canvasSize)

        return renderer.image { _ in
            let circleRect = CGRect(origin: .zero, size: canvasSize)
                .insetBy(dx: strokeWidth / 2, dy: strokeWidth / 2)
            let circlePath = UIBezierPath(ovalIn: circleRect)
            circlePath.lineWidth = strokeWidth
            fillColor.setFill()
            strokeColor.setStroke()
            circlePath.fill()
            circlePath.stroke()

            let text = "\(count)" as NSString
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.alignment = .center
            let attributes: [NSAttributedString.Key: Any] = [
                .font: UIFont.systemFont(ofSize: 14, weight: .medium),
                .foregroundColor: UIColor.black,
                .paragraphStyle: paragraphStyle
            ]
            let textSize = text.size(withAttributes: attributes)
            let textRect = CGRect(
                x: 0,
                y: (size - textSize.height) / 2,
                width: size,
                height: textSize.height
            )
            text.draw(in: textRect, withAttributes: attributes)
        }
    }
}
