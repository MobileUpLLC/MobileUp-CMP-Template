package ru.mobileup.template.features.places

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.places.data.PlacesRepository
import ru.mobileup.template.features.places.data.PlacesRepositoryImpl
import ru.mobileup.template.features.places.presentation.PlacesComponent
import ru.mobileup.template.features.places.presentation.RealPlacesComponent

val placesModule = module {
    single<PlacesRepository> { PlacesRepositoryImpl(get()) }
}

fun ComponentFactory.createPlacesComponent(
    componentContext: ComponentContext
): PlacesComponent {
    return RealPlacesComponent(componentContext, get(), get(), get(), get(), get(), get())
}
