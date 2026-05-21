package ru.mobileup.template.core.message.domain

import org.jetbrains.compose.resources.DrawableResource
import ru.mobileup.template.core.utils.StringDesc
import ru.mobileup.template.core.utils.desc

data class Message(
    val text: StringDesc,
    val iconRes: DrawableResource? = null,
    val actionTitle: StringDesc? = null,
    val action: (() -> Unit)? = null
) {

    companion object {
        val FAKE = Message(
            text = "Message".desc()
        )
    }
}
