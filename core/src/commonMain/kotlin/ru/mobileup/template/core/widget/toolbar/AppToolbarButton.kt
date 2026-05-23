package ru.mobileup.template.core.widget.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.generated.resources.Res
import ru.mobileup.template.core.generated.resources.common_back
import ru.mobileup.template.core.generated.resources.ic_arrow_back_24

@Composable
fun AppToolbarButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppToolbarDefaults.buttonTint
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
fun AppToolbarBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppToolbarButton(
        painter = painterResource(Res.drawable.ic_arrow_back_24),
        contentDescription = stringResource(Res.string.common_back),
        onClick = onClick,
        modifier = modifier,
    )
}
