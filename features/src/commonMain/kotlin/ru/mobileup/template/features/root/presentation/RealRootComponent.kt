package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.template.core.utils.safePush
import ru.mobileup.template.core.utils.toStateFlow
import ru.mobileup.template.features.menu.domain.Sample
import ru.mobileup.template.features.menu.presentation.MenuComponent

class RealRootComponent(
    componentContext: ComponentContext,
    private val childComponentFactory: RootChildComponentFactory
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Menu,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override val messageComponent = childComponentFactory.createMessageComponent(
        childContext(key = "message")
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        ChildConfig.Menu -> {
            RootComponent.Child.Menu(
                childComponentFactory.createMenuComponent(
                    componentContext,
                    ::onMenuOutput
                )
            )
        }

        ChildConfig.Pokemons -> {
            RootComponent.Child.Pokemons(
                childComponentFactory.createPokemonsComponent(componentContext)
            )
        }

        ChildConfig.Dialogs -> {
            RootComponent.Child.Dialogs(
                childComponentFactory.createDialogsComponent(componentContext)
            )
        }

        ChildConfig.Permission -> {
            RootComponent.Child.Permission(
                childComponentFactory.createPermissionComponent(componentContext)
            )
        }

        ChildConfig.Settings -> {
            RootComponent.Child.Settings(
                childComponentFactory.createSettingsComponent(componentContext)
            )
        }

        ChildConfig.Places -> {
            RootComponent.Child.Places(
                childComponentFactory.createPlacesComponent(componentContext)
            )
        }
    }

    private fun onMenuOutput(output: MenuComponent.Output) {
        when (output) {
            is MenuComponent.Output.SampleRequested -> {
                navigation.safePush(output.sample.toChildConfig())
            }
        }
    }

    override fun onBack() = navigation.pop()

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object Menu : ChildConfig

        @Serializable
        data object Pokemons : ChildConfig

        @Serializable
        data object Dialogs : ChildConfig

        @Serializable
        data object Permission : ChildConfig

        @Serializable
        data object Settings : ChildConfig

        @Serializable
        data object Places : ChildConfig
    }

    private fun Sample.toChildConfig(): ChildConfig = when (this) {
        Sample.Pokemons -> ChildConfig.Pokemons
        Sample.Dialogs -> ChildConfig.Dialogs
        Sample.Permission -> ChildConfig.Permission
        Sample.Settings -> ChildConfig.Settings
        Sample.Places -> ChildConfig.Places
    }
}
