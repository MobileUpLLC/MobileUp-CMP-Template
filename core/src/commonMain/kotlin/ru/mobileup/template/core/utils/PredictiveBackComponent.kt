package ru.mobileup.template.core.utils

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.backhandler.BackHandler

/**
 * Should be implemented by components with ChildStack to support predictive back animation
 */
interface PredictiveBackComponent : BackHandlerOwner {
    fun onBack()
}

/**
 * A convenient method to create [com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation]
 * for components implementing [PredictiveBackComponent].
 */
@Composable
fun <C : Any, T : Any> PredictiveBackComponent.predictiveBackAnimation(): StackAnimation<C, T> {
    return createPlatformPredictiveBackAnimation(backHandler, ::onBack)
}

internal expect fun <C : Any, T : Any> createPlatformPredictiveBackAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit
): StackAnimation<C, T>
