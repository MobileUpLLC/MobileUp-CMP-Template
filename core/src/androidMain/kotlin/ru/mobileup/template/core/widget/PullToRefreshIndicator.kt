package ru.mobileup.template.core.widget

import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.mobileup.template.core.theme.custom.CustomTheme

@Composable
internal actual fun PullToRefreshIndicator(
    modifier: Modifier,
    state: PullToRefreshState,
    isRefreshing: Boolean
) {
    PullToRefreshDefaults.Indicator(
        modifier = modifier,
        state = state,
        isRefreshing = isRefreshing,
        containerColor = CustomTheme.colors.background.surface,
        color = CustomTheme.colors.icon.primary
    )
}
