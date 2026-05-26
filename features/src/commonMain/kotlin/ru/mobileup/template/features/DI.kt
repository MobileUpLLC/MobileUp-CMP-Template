package ru.mobileup.template.features

import ru.mobileup.template.features.dialogs.dialogsModule
import ru.mobileup.template.features.menu.menuModule
import ru.mobileup.template.features.permission.permissionModule
import ru.mobileup.template.features.places.placesModule
import ru.mobileup.template.features.pokemons.pokemonsModule
import ru.mobileup.template.features.settings.settingsModule

val featureModules = listOf(
    menuModule,
    pokemonsModule,
    dialogsModule,
    permissionModule,
    settingsModule,
    placesModule
)
