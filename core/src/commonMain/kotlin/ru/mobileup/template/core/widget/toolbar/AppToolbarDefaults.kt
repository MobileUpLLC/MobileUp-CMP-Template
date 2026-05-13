package ru.mobileup.template.core.widget.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.configuration.PlatformType
import ru.mobileup.template.core.theme.custom.CustomTheme

@Immutable
object AppToolbarDefaults {

    @Stable
    val backgroundColor: Color
        @Composable
        get() = CustomTheme.colors.background.screen

    @Stable
    val contentColor: Color
        @Composable
        get() = CustomTheme.colors.text.primary

    @Stable
    val titleTextStyle: TextStyle
        @Composable
        get() = CustomTheme.typography.title.regular

    @Stable
    val buttonTint: Color
        @Composable
        get() = CustomTheme.colors.icon.primary

    @Stable
    @Composable
    fun elevation(platformType: PlatformType): Dp = when (platformType) {
        PlatformType.Android -> 4.dp
        PlatformType.Ios -> 0.dp
    }

    val TitleMinHeight = 48.dp

    val dividerThickness = 0.5.dp
}
