package ru.mobileup.template.features.pokemons.presentation

import com.arkivanov.essenty.backhandler.BackDispatcher
import ru.mobileup.template.core.utils.createFakeChildStackStateFlow
import ru.mobileup.template.features.pokemons.presentation.list.FakePokemonListComponent

class FakePokemonsComponent : PokemonsComponent {

    override val childStack = createFakeChildStackStateFlow(
        PokemonsComponent.Child.List(FakePokemonListComponent())
    )

    override val backHandler = BackDispatcher()

    override fun onBack() = Unit
}
