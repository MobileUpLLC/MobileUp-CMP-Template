package ru.mobileup.template.core.widget.switch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme

@Composable
actual fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    colors: AppSwitchColors,
) {
    val controlColors = CustomTheme.colors.control

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colors.thumbColor,
            checkedTrackColor = colors.checkedTrackColor,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = colors.thumbColor,
            uncheckedTrackColor = controlColors.unselectedContainer,
            uncheckedBorderColor = Color.Transparent,
            disabledCheckedThumbColor = controlColors.contentDisabled,
            disabledCheckedTrackColor = controlColors.selectedContainerDisabled,
            disabledCheckedBorderColor = Color.Transparent,
            disabledUncheckedThumbColor = controlColors.contentDisabled,
            disabledUncheckedTrackColor = controlColors.unselectedContainerDisabled,
            disabledUncheckedBorderColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppSwitchPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppSwitchPreviewItem(text = "Enabled checked", checked = true, enabled = true)
            AppSwitchPreviewItem(text = "Enabled unchecked", checked = false, enabled = true)
            AppSwitchPreviewItem(text = "Disabled checked", checked = true, enabled = false)
            AppSwitchPreviewItem(text = "Disabled unchecked", checked = false, enabled = false)
        }
    }
}

@Composable
private fun AppSwitchPreviewItem(
    text: String,
    checked: Boolean,
    enabled: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppSwitch(
            checked = checked,
            onCheckedChange = {},
            enabled = enabled
        )
        Text(
            text = text,
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.body.regular
        )
    }
}
