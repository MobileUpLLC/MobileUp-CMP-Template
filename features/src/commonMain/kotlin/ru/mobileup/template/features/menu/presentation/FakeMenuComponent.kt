package ru.mobileup.template.features.menu.presentation

import ru.mobileup.template.features.menu.domain.Sample

class FakeMenuComponent(
    private val onOutput: (MenuComponent.Output) -> Unit = {}
) : MenuComponent {

    override val samples = listOf(
        Sample.Pokemons,
        Sample.Dialogs,
        Sample.Permission,
        Sample.Settings
    )

    override fun onSampleClick(sample: Sample) = Unit

    fun emitOutput(output: MenuComponent.Output) {
        onOutput(output)
    }
}
