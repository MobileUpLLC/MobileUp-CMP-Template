package ru.mobileup.template.core.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.configuration.LocalPlatformType
import ru.mobileup.template.core.configuration.PlatformType
import ru.mobileup.template.core.generated.resources.Res
import ru.mobileup.template.core.generated.resources.common_back
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.LocalBackAction

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
    backgroundColor: Color = CustomTheme.colors.background.screen,
    contentColor: Color = CustomTheme.colors.text.primary,
    actions: @Composable RowScope.() -> Unit = {},
    bottomContent: @Composable ColumnScope.() -> Unit = {},
) {
    val platformType = LocalPlatformType.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = when (platformType) {
            PlatformType.Android -> 4.dp
            PlatformType.Ios -> 0.dp
        }
    ) {
        Column(Modifier.statusBarsPadding()) {
            when (titleAlignment) {
                AppToolbarTitleAlignment.Start -> AppToolbarStartContent(
                    title = title,
                    showBackButton = showBackButton,
                    onBackClick = onBackClick,
                    contentColor = contentColor,
                    actions = actions
                )

                AppToolbarTitleAlignment.Center -> AppToolbarCenterContent(
                    title = title,
                    showBackButton = showBackButton,
                    onBackClick = onBackClick,
                    contentColor = contentColor,
                    actions = actions
                )
            }

            bottomContent()

            if (platformType == PlatformType.Ios) {
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun AppToolbarButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = CustomTheme.colors.icon.primary
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
private fun AppToolbarStartContent(
    title: String,
    showBackButton: Boolean,
    onBackClick: (() -> Unit)?,
    contentColor: Color,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            AppToolbarBackButton(onBackClick = onBackClick)
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
            style = CustomTheme.typography.title.regular,
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
    actions: @Composable RowScope.() -> Unit,
) {
    StartCenterEndLayout(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        start = {
            if (showBackButton) {
                AppToolbarBackButton(onBackClick = onBackClick)
            }
        },
        center = {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                color = contentColor,
                style = CustomTheme.typography.title.regular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        end = {
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
    )
}

@Composable
private fun AppToolbarBackButton(
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val backAction = onBackClick ?: LocalBackAction.current

    AppToolbarButton(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(Res.string.common_back),
        onClick = backAction,
        modifier = modifier,
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
