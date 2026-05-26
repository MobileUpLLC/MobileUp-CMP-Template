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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.ic_cloudy_24
import ru.mobileup.template.features.generated.resources.ic_day_24
import ru.mobileup.template.features.generated.resources.ic_night_24

@Composable
fun MapThemeSwitch(
    theme: MapTheme,
    onThemeSwitch: (MapTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = mapControlColors(theme)

    Column(modifier = modifier) {
        ThemeButton(
            icon = Res.drawable.ic_day_24,
            selected = theme == MapTheme.Bright,
            colors = colors,
            onClick = { onThemeSwitch(MapTheme.Bright) }
        )
        ThemeButton(
            icon = Res.drawable.ic_cloudy_24,
            selected = theme == MapTheme.Default,
            colors = colors,
            onClick = { onThemeSwitch(MapTheme.Default) }
        )
        ThemeButton(
            icon = Res.drawable.ic_night_24,
            selected = theme == MapTheme.Dark,
            colors = colors,
            onClick = { onThemeSwitch(MapTheme.Dark) }
        )
    }
}

@Composable
private fun ThemeButton(
    icon: DrawableResource,
    selected: Boolean,
    colors: MapControlColors,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        if (selected) colors.selectedBackground else colors.background
    )
    val borderColor by animateColorAsState(colors.border)
    val iconColor by animateColorAsState(colors.icon)

    IconButton(onClick = onClick) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
                .drawBehind { drawRect(backgroundColor) }
                .border(1.dp, borderColor, CircleShape)
                .padding(8.dp),
            painter = painterResource(icon),
            tint = iconColor,
            contentDescription = null
        )
    }
}
