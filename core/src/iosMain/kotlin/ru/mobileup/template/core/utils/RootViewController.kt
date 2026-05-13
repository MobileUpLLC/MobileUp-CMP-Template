package ru.mobileup.template.core.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController

class RootViewController(
    private val backDispatcher: BackDispatcher,
    private val rootUi: @Composable () -> Unit
) : UIViewController(nibName = null, bundle = null) {

    private var darkStatusBarIcons = true

    private val composeViewController = ComposeUIViewController { content() }

    private val systemBarIconsColorHandler = object : SystemBarIconsColorHandler {
        override fun updateSystemBarIconsColor(
            darkStatusBarIcons: Boolean,
            darkNavigationBarIcons: Boolean // is not used on iOS
        ) {
            this@RootViewController.darkStatusBarIcons = darkStatusBarIcons
            setNeedsStatusBarAppearanceUpdate()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun viewDidLoad() {
        super.viewDidLoad()

        addChildViewController(composeViewController)
        view.addSubview(composeViewController.view)
        composeViewController.view.setFrame(view.bounds)
        composeViewController.didMoveToParentViewController(this)
    }

    @Composable
    private fun content() {
        @OptIn(ExperimentalDecomposeApi::class)
        PredictiveBackGestureOverlay(
            modifier = Modifier.fillMaxSize(),
            backDispatcher = backDispatcher,
            backIcon = null
        ) {
            CompositionLocalProvider(
                LocalSystemBarIconsColorHandler provides systemBarIconsColorHandler
            ) {
                rootUi()
            }
        }
    }

    override fun preferredStatusBarStyle(): UIStatusBarStyle {
        return if (darkStatusBarIcons) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
    }
}
