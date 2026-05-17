package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.message.presentation.FakeMessageComponent
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.features.pokemons.presentation.FakePokemonsComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent

class TestRootChildComponentFactory : RootChildComponentFactory {

    override fun createPokemonsComponent(componentContext: ComponentContext): PokemonsComponent {
        return FakePokemonsComponent()
    }

    override fun createMessageComponent(componentContext: ComponentContext): MessageComponent {
        return FakeMessageComponent()
    }
}
