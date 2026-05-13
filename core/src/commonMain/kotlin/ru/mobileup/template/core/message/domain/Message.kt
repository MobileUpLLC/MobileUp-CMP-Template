package ru.mobileup.template.core.message.domain

import org.jetbrains.compose.resources.DrawableResource
import ru.mobileup.template.core.utils.StringDesc

data class Message(
    val text: StringDesc,
    val iconRes: DrawableResource? = null,
    val actionTitle: StringDesc? = null,
    val action: (() -> Unit)? = null
)
