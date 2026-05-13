package ru.mobileup.template.features.pokemons.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.configuration.LocalPlatformType
import ru.mobileup.template.core.configuration.PlatformType
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.features.pokemons.domain.PokemonType

@Composable
fun PokemonTypeItem(
    type: PokemonType,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(48.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = if (!isSelected || LocalPlatformType.current == PlatformType.Android) {
                    6.dp
                } else {
                    2.dp
                },
                shape = shape
            )
            .background(
                color = when (isSelected) {
                    true -> CustomTheme.colors.button.primary
                    else -> CustomTheme.colors.button.secondary
                },
                shape = shape
            )
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
    ) {
        Text(
            text = type.name,
            style = CustomTheme.typography.body.regular,
            color = if (isSelected) {
                CustomTheme.colors.text.invert
            } else {
                CustomTheme.colors.text.primary
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun PokemonTypeItemPreview() {
    var isSelected by remember { mutableStateOf(false) }
    AppTheme {
        PokemonTypeItem(
            type = PokemonType.Fire,
            isSelected = isSelected,
            onClick = {
                isSelected = !isSelected
            }
        )
    }
}
