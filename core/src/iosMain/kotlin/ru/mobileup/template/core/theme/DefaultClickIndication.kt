package ru.mobileup.template.core.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

actual fun defaultClickIndication(): Indication = IosClickIndication

private object IosClickIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return IosClickIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean = other === IosClickIndication
    override fun hashCode() = 100
}

private class IosClickIndicationNode(private val interactionSource: InteractionSource) :
    Modifier.Node(), DrawModifierNode {

    val animatedAlpha = Animatable(1.0f)

    private val layerPaint = Paint()

    private suspend fun animateToPressed() {
        animatedAlpha.animateTo(0.5f, spring())
    }

    private suspend fun animateToResting() {
        animatedAlpha.animateTo(1.0f, spring())
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed()
                    is PressInteraction.Release -> animateToResting()
                    is PressInteraction.Cancel -> animateToResting()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val alpha = animatedAlpha.value

        if (alpha >= 0.999f) {
            drawContent()
            return
        }

        layerPaint.alpha = alpha

        drawContext.canvas.saveLayer(
            bounds = Rect(offset = Offset.Zero, size = size),
            paint = layerPaint
        )

        drawContent()

        drawContext.canvas.restore()
    }
}