package ru.mobileup.template.features.menu.presentation

import ru.mobileup.template.features.menu.domain.Sample

interface MenuComponent {

    val samples: List<Sample>

    fun onSampleClick(sample: Sample)

    sealed interface Output {
        data class SampleRequested(val sample: Sample) : Output
    }
}
