package ru.mobileup.template.core.widget.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.configuration.LocalPlatformType
import ru.mobileup.template.core.configuration.PlatformType
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.widget.StartCenterEndLayout

enum class AppToolbarTitleAlignment {
    Start,
    Center
}

@Composable
fun AppToolbar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    titleAlignment: AppToolbarTitleAlignment = AppToolbarTitleAlignment.Start,
    backgroundColor: Color = AppToolbarDefaults.backgroundColor,
    contentColor: Color = AppToolbarDefaults.contentColor,
    titleTextStyle: TextStyle = AppToolbarDefaults.titleTextStyle,
    elevation: Dp = AppToolbarDefaults.elevation(LocalPlatformType.current),
    actions: @Composable RowScope.() -> Unit = {},
    bottomContent: @Composable ColumnScope.() -> Unit = {},
) {
    val platformType = LocalPlatformType.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (elevation > 0.dp) {
                    Modifier.shadow(elevation)
                } else {
                    Modifier
                }
            )
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        when (titleAlignment) {
            AppToolbarTitleAlignment.Start -> AppToolbarStartContent(
                title = title,
                showBackButton = showBackButton,
                onBackClick = onBackClick,
                contentColor = contentColor,
                titleTextStyle = titleTextStyle,
                actions = actions
            )

            AppToolbarTitleAlignment.Center -> AppToolbarCenterContent(
                title = title,
                showBackButton = showBackButton,
                onBackClick = onBackClick,
                contentColor = contentColor,
                titleTextStyle = titleTextStyle,
                actions = actions
            )
        }

        bottomContent()

        if (platformType == PlatformType.Ios) {
            HorizontalDivider(thickness = AppToolbarDefaults.dividerThickness)
        }
    }
}

@Composable
private fun AppToolbarStartContent(
    title: String,
    showBackButton: Boolean,
    onBackClick: (() -> Unit)?,
    contentColor: Color,
    titleTextStyle: TextStyle,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppToolbarDefaults.TitleMinHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            AppToolbarBackButton(onClick = onBackClick)
        }

        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (showBackButton) 0.dp else 16.dp,
                    end = 16.dp
                ),
            color = contentColor,
            style = titleTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

@Composable
private fun AppToolbarCenterContent(
    title: String,
    showBackButton: Boolean,
    onBackClick: (() -> Unit)?,
    contentColor: Color,
    titleTextStyle: TextStyle,
    actions: @Composable RowScope.() -> Unit,
) {
    StartCenterEndLayout(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppToolbarDefaults.TitleMinHeight),
        start = {
            if (showBackButton) {
                AppToolbarBackButton(onClick = onBackClick)
            }
        },
        center = {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                color = contentColor,
                style = titleTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        end = {
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
    )
}

@Preview
@Composable
private fun AppToolbarPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppToolbar(
                title = "Toolbar",
                showBackButton = true,
                titleAlignment = AppToolbarTitleAlignment.Start,
                onBackClick = {}
            )

            AppToolbar(
                title = "Toolbar",
                titleAlignment = AppToolbarTitleAlignment.Center,
                onBackClick = {},
                actions = {
                    AppToolbarButton(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        onClick = {}
                    )
                }
            )

            AppToolbar(
                title = "Very long toolbar title with trailing actions",
                showBackButton = true,
                titleAlignment = AppToolbarTitleAlignment.Center,
                onBackClick = {},
                actions = {
                    AppToolbarButton(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        onClick = {}
                    )
                    AppToolbarButton(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        onClick = {}
                    )
                }
            )

            AppToolbar(
                title = "Toolbar",
                titleAlignment = AppToolbarTitleAlignment.Center,
                onBackClick = {},
                actions = {
                    AppToolbarButton(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        onClick = {}
                    )
                },
                bottomContent = {
                    Text(
                        modifier = Modifier.fillMaxWidth()
                            .padding(24.dp),
                        text = "Toolbar content",
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}
