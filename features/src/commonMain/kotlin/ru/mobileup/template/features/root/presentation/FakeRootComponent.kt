package ru.mobileup.template.features.root.presentation

import com.arkivanov.essenty.backhandler.BackDispatcher
import ru.mobileup.template.core.message.presentation.FakeMessageComponent
import ru.mobileup.template.core.utils.createFakeChildStackStateFlow
import ru.mobileup.template.features.menu.presentation.FakeMenuComponent

class FakeRootComponent : RootComponent {

    override val childStack = createFakeChildStackStateFlow(
        RootComponent.Child.Menu(FakeMenuComponent())
    )

    override val backHandler = BackDispatcher()

    override val messageComponent = FakeMessageComponent()

    override fun onBack() = Unit
}
