package ru.mobileup.template.features.menu.presentation

import ru.mobileup.template.features.menu.domain.Sample

class FakeMenuComponent(
    private val onOutput: (MenuComponent.Output) -> Unit = {}
) : MenuComponent {

    override val samples = Sample.entries

    override fun onSampleClick(sample: Sample) = Unit

    fun emitOutput(output: MenuComponent.Output) {
        onOutput(output)
    }
}
