package ru.mobileup.template.core.utils

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import me.aartikov.replica.paged.PagedLoadingStatus

@Composable
fun LazyGridState.TriggerLoadNext(
    pagedState: PagedState<*>,
    hasNextPage: Boolean,
    callback: () -> Unit,
    itemCountGap: Int = 3
) {
    if (hasNextPage && pagedState.loadingStatus == PagedLoadingStatus.None) {
        OnEndReached(
            callback = callback,
            itemCountGap = itemCountGap,
            scrollingToEndRequired = pagedState.error != null
        )
    }
}

@Composable
private fun LazyGridState.OnEndReached(
    callback: () -> Unit,
    itemCountGap: Int,
    scrollingToEndRequired: Boolean
) {
    val latestCallback by rememberUpdatedState(callback)

    val endReached by remember(this) {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - itemCountGap
        }
    }

    if (endReached && (!scrollingToEndRequired || isScrollingToEnd())) {
        LaunchedEffect(Unit) {
            latestCallback()
        }
    }
}

@Composable
private fun LazyGridState.isScrollingToEnd(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex < firstVisibleItemIndex
            } else {
                previousScrollOffset < firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value && isScrollInProgress
}
