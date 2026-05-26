package ru.mobileup.template.features.places.presentation.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.ic_minus_24
import ru.mobileup.template.features.generated.resources.ic_plus_24

@Composable
fun MapZoomButtons(
    theme: MapTheme,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = mapControlColors(theme)
    val backgroundColor by animateColorAsState(colors.background)
    val borderColor by animateColorAsState(colors.border)
    val iconColor by animateColorAsState(colors.icon)

    val iconModifier = Modifier
        .clip(CircleShape)
        .size(48.dp)
        .drawBehind { drawRect(backgroundColor) }
        .border(1.dp, borderColor, CircleShape)
        .padding(8.dp)

    Column(modifier = modifier) {
        IconButton(onClick = onZoomInClick) {
            Icon(
                modifier = iconModifier,
                painter = painterResource(Res.drawable.ic_plus_24),
                tint = iconColor,
                contentDescription = null
            )
        }
        IconButton(onClick = onZoomOutClick) {
            Icon(
                modifier = iconModifier,
                painter = painterResource(Res.drawable.ic_minus_24),
                tint = iconColor,
                contentDescription = null
            )
        }
    }
}
