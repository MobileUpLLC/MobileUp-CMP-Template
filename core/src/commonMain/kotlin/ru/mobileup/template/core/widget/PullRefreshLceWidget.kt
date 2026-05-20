package ru.mobileup.template.core.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.configuration.LocalPlatformType
import ru.mobileup.template.core.configuration.PlatformType
import ru.mobileup.template.core.utils.AbstractLoadableState
import ru.mobileup.template.core.utils.StringDesc
import ru.mobileup.template.core.utils.plus

/**
 * A pull-to-refresh version of [LceWidget]. It renders loading, error, and content states,
 * and also wraps the loaded content with pull-to-refresh behavior.
 *
 * @param state - state to render.
 * If data is available, the content is shown inside the pull-to-refresh container.
 * @param innerPadding padding that the widget applies to its built-in UI states,
 * including loading, error, and the pull-to-refresh indicator.
 * It is also passed to the main content as `contentPadding`.
 * @param onRefresh callback triggered by the pull-to-refresh gesture.
 * @param onRetryClick callback used by error UI, it equals to [onRefresh] by default.
 * @param refreshOverlay overlay UI shown over loaded content while data is refreshing.
 * The second boolean is true when refresh was triggered by pull-to-refresh.
 * Pass `null` to disable it.
 * @param applyImePadding whether to include IME bottom padding in the padding passed to loading, error, and content.
 * @param loadingContent loading UI that receives effective padding and is expected to respect it.
 * @param errorContent error UI that receives effective padding and is expected to respect it.
 * @param content main content of the widget. The provided `contentPadding` should be applied
 * by the caller to the main content so it respects the safe area.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> PullRefreshLceWidget(
    state: AbstractLoadableState<T>,
    innerPadding: PaddingValues,
    onRefresh: () -> Unit,
    onRetryClick: () -> Unit = onRefresh,
    modifier: Modifier = Modifier,
    refreshOverlay: (@Composable BoxScope.(
        visible: Boolean,
        contentPadding: PaddingValues
    ) -> Unit)? = { visible, contentPadding ->
        RefreshingProgress(
            modifier = Modifier
                .padding(contentPadding),
            visible = visible
        )
    },
    loadingContent: @Composable BoxScope.(PaddingValues) -> Unit = { paddings ->
        FullscreenCircularProgress(
            modifier = Modifier.padding(paddings)
        )
    },
    errorContent: @Composable BoxScope.(StringDesc, PaddingValues) -> Unit = { error, paddings ->
        ErrorPlaceholder(
            modifier = Modifier.padding(paddings),
            errorMessage = error.resolve(),
            onRetryClick = onRetryClick
        )
    },
    applyImePadding: Boolean = true,
    content: @Composable BoxScope.(data: T, contentPadding: PaddingValues) -> Unit,
) {
    var pullRefreshTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(state.loading) {
        if (!state.loading) pullRefreshTriggered = false
    }

    LceWidget(
        state = state,
        innerPadding = innerPadding,
        onRetryClick = onRetryClick,
        modifier = modifier,
        refreshOverlay = null,
        applyImePadding = applyImePadding,
        loadingContent = loadingContent,
        errorContent = errorContent,
    ) { data, contentPadding ->
        val pullRefreshState = rememberPullToRefreshState()
        val isPullRefreshRefreshing = state.loading && pullRefreshTriggered
        val refreshOverlayVisible = state.loading && !pullRefreshTriggered

        PullToRefreshBox(
            isRefreshing = isPullRefreshRefreshing,
            enabled = !refreshOverlayVisible,
            state = pullRefreshState,
            onRefresh = {
                pullRefreshTriggered = true
                onRefresh()
            },
            indicator = {
                PullToRefreshIndicator(
                    modifier = Modifier
                        .padding(contentPadding)
                        .align(Alignment.TopCenter),
                    state = pullRefreshState,
                    isRefreshing = isPullRefreshRefreshing
                )
            },
        ) {
            val pullRefreshTopPadding = calculatePullRefreshTopPadding(
                pullRefreshState = pullRefreshState,
                pullRefreshTriggered = pullRefreshTriggered,
                refreshDistance = when (LocalPlatformType.current) {
                    PlatformType.Android -> 0.dp
                    PlatformType.Ios -> 80.dp
                }
            )
            content(data, contentPadding + PaddingValues(top = pullRefreshTopPadding))
        }

        if(refreshOverlay != null) {
            refreshOverlay(refreshOverlayVisible, contentPadding)
        }
    }
}

@Composable
private fun calculatePullRefreshTopPadding(
    pullRefreshState: PullToRefreshState,
    pullRefreshTriggered: Boolean,
    refreshDistance: Dp
): Dp {
    if (refreshDistance == 0.dp) return 0.dp

    val targetValue = when {
        pullRefreshTriggered -> refreshDistance
        else -> refreshDistance * pullRefreshState.distanceFraction
    }

    val shouldFollowFinger = !pullRefreshTriggered && pullRefreshState.distanceFraction > 0f

    val animatedValue by animateDpAsState(
        targetValue = targetValue,
        animationSpec = if (shouldFollowFinger) {
            snap()
        } else {
            spring()
        },
        label = "PullRefreshTopPadding"
    )

    return animatedValue
}

@Composable
internal expect fun PullToRefreshIndicator(
    modifier: Modifier,
    state: PullToRefreshState,
    isRefreshing: Boolean
)
