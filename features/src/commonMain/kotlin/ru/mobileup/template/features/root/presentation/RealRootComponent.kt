package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.Serializable
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.createMessageComponent
import ru.mobileup.template.core.utils.toStateFlow
import ru.mobileup.template.features.pokemons.createPokemonsComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.settingsdemo.createSettingsDemoComponent

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.SettingsDemo,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override val messageComponent = componentFactory.createMessageComponent(
        childContext(key = "message")
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        is ChildConfig.Pokemons -> {
            RootComponent.Child.Pokemons(
                componentFactory.createPokemonsComponent(componentContext, ::onPokemonsOutput)
            )
        }

        is ChildConfig.SettingsDemo -> {
            RootComponent.Child.SettingsDemo(
                componentFactory.createSettingsDemoComponent(componentContext)
            )
        }
    }

    private fun onPokemonsOutput(output: PokemonsComponent.Output) {
        when (output) {
            PokemonsComponent.Output.SettingsDemoRequested -> navigation.pushNew(ChildConfig.SettingsDemo)
        }
    }

    override fun onBack() = navigation.pop()

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object Pokemons : ChildConfig

        @Serializable
        data object SettingsDemo : ChildConfig
    }
}
