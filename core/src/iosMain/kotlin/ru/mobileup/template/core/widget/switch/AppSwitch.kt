package ru.mobileup.template.core.widget.switch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UISwitch
import platform.UIKit.UIView
import ru.mobileup.template.core.utils.toUIColor

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    colors: AppSwitchColors
) {
    val onCheckedChangeState = rememberUpdatedState(onCheckedChange)

    UIKitView(
        modifier = modifier,
        factory = {
            AppSwitchView(
                onCheckedChange = { onCheckedChangeState.value(it) }
            )
        },
        update = { uiSwitch ->
            uiSwitch.update(
                checked = checked,
                enabled = enabled,
                colors = colors,
            )
        }
    )
}

private const val HORIZONTAL_CONTENT_INSET = 2.0

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class AppSwitchView(
    private val onCheckedChange: (Boolean) -> Unit,
) : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {

    private val uiSwitch = UISwitch(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))

    private var pendingUserChecked: Boolean? = null
    private var appliedColors: AppSwitchColors? = null

    init {
        addSubview(uiSwitch)
        uiSwitch.addTarget(
            target = this,
            action = NSSelectorFromString(this::valueChanged.name),
            forControlEvents = UIControlEventValueChanged
        )
    }

    override fun intrinsicContentSize() = uiSwitch.intrinsicContentSize().useContents {
        CGSizeMake(
            width = width + HORIZONTAL_CONTENT_INSET * 2, // small horizontal padding to fix clipping
            height = height
        )
    }

    override fun layoutSubviews() {
        super.layoutSubviews()

        val boundsHeight = bounds.useContents { size.height }
        uiSwitch.intrinsicContentSize().useContents {
            uiSwitch.setFrame(
                CGRectMake(
                    x = HORIZONTAL_CONTENT_INSET,
                    y = (boundsHeight - height) / 2.0,
                    width = width,
                    height = height
                )
            )
        }
    }

    fun update(
        checked: Boolean,
        enabled: Boolean,
        colors: AppSwitchColors,
    ) {
        val pendingChecked = pendingUserChecked
        val shouldSyncChecked = when {
            pendingChecked == null -> true
            checked == pendingChecked -> {
                pendingUserChecked = null
                true
            }

            else -> false
        }

        if (shouldSyncChecked && checked != uiSwitch.on) {
            uiSwitch.setOn(checked, animated = false)
        }

        if (uiSwitch.enabled != enabled) {
            uiSwitch.enabled = enabled
        }

        if (appliedColors != colors) {
            uiSwitch.onTintColor = colors.checkedTrackColor.toUIColor()
            uiSwitch.thumbTintColor = colors.thumbColor.toUIColor()
            appliedColors = colors
        }
    }

    @ObjCAction
    fun valueChanged() {
        pendingUserChecked = uiSwitch.on
        onCheckedChange(uiSwitch.on)
    }
}
