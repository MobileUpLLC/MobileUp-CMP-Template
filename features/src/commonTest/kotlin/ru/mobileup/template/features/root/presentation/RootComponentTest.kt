package ru.mobileup.template.features.root.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import ru.mobileup.template.core.utils.activeChild
import ru.mobileup.template.features.root.createRootComponent
import ru.mobileup.template.features.utils.componentTest

class RootComponentTest : FunSpec({

    componentTest("shows pokemons flow as initial screen") {
        // 🛠️ Prepare root flow
        val childComponentFactory = FakeRootChildComponentFactory()
        val component = setupComponent {
            createRootComponent(it, childComponentFactory)
        }

        // ✅ Verify pokemons flow is shown initially
        component.childStack.activeChild.shouldBeInstanceOf<RootComponent.Child.Pokemons>()
    }
})
