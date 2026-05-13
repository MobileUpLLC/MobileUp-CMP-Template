package ru.mobileup.template.core.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal actual fun PullToRefreshIndicator(
    modifier: Modifier,
    state: PullToRefreshState,
    isRefreshing: Boolean
) {

    AnimatedVisibility(
        modifier = modifier.padding(top = 32.dp),
        visible = state.distanceFraction > 0f || isRefreshing,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        var thresholdReached by remember { mutableStateOf(false) }
        SideEffect {
            if (state.distanceFraction > 1.0f) {
                thresholdReached = true
            }
        }

        CupertinoActivityIndicator(
            progress = when {
                isRefreshing || thresholdReached -> 1.0f
                else -> state.distanceFraction.coerceIn(0.0f, 1.0f)
            }
        )
    }
}