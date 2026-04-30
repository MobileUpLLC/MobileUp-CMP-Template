package ru.mobileup.template.core.configuration

enum class PlatformType {
    Android,
    Ios
}

expect class Platform {
    val type: PlatformType
}
