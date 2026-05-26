package ru.mobileup.template.core.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.blockGestures

@Composable
actual fun FullscreenCircularProgress(modifier: Modifier, overlay: Boolean) {
    Box(
        modifier = modifier
            .run {
                if (overlay) {
                    background(CustomTheme.colors.background.screen.copy(alpha = 0.7f))
                        .blockGestures()
                } else {
                    this
                }
            }
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
