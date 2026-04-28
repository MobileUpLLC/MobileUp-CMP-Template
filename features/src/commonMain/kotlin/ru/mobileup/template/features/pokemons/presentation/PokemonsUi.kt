package ru.mobileup.template.features.pokemons.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.utils.predictiveBackAnimation
import ru.mobileup.template.features.pokemons.presentation.dialogs_demo.DialogsDemoUi
import ru.mobileup.template.features.pokemons.presentation.details.PokemonDetailsUi
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListUi

@Composable
fun PokemonsUi(
    component: PokemonsComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.collectAsState()

    Children(
        stack = childStack,
        modifier = modifier,
        animation = component.predictiveBackAnimation()
    ) { child ->
        when (val instance = child.instance) {
            is PokemonsComponent.Child.List -> PokemonListUi(instance.component)
            is PokemonsComponent.Child.Details -> PokemonDetailsUi(instance.component)
            is PokemonsComponent.Child.DialogsDemo -> DialogsDemoUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun PokemonsUiPreview() {
    AppTheme {
        PokemonsUi(FakePokemonsComponent())
    }
}
