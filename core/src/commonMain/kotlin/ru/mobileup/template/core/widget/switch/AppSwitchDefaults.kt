package ru.mobileup.template.core.widget.switch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import ru.mobileup.template.core.theme.custom.CustomTheme

@Immutable
object AppSwitchDefaults {

    @Stable
    @Composable
    fun colors(
        checkedTrackColor: Color = CustomTheme.colors.control.selectedContainer,
        thumbColor: Color = CustomTheme.colors.control.content,
    ) = AppSwitchColors(
        checkedTrackColor = checkedTrackColor,
        thumbColor = thumbColor
    )
}
