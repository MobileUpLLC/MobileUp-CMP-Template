package ru.mobileup.template.features.menu.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.features.menu.domain.Sample

class RealMenuComponent(
    componentContext: ComponentContext,
    private val onOutput: (MenuComponent.Output) -> Unit
) : ComponentContext by componentContext, MenuComponent {

    override val samples = Sample.entries

    override fun onSampleClick(sample: Sample) {
        onOutput(MenuComponent.Output.SampleRequested(sample))
    }
}
