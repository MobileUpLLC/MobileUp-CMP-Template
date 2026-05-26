package ru.mobileup.template.features.places.presentation.widget

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.ic_location_24

@Composable
fun MapMyLocationButton(
    theme: MapTheme,
    onClick: () -> Unit,
    isLocationSearchInProgress: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = mapControlColors(theme)
    val backgroundColor by animateColorAsState(colors.background)
    val borderColor by animateColorAsState(colors.border)
    val iconColor by animateColorAsState(colors.icon)

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(backgroundColor, CircleShape)
                .border(1.dp, borderColor, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(isLocationSearchInProgress) { inProgress ->
                if (inProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                        color = iconColor
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_location_24),
                        contentDescription = null,
                        tint = iconColor
                    )
                }
            }
        }
    }
}
