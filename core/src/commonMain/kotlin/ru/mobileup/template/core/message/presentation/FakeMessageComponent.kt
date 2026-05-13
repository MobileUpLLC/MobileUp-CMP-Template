package ru.mobileup.template.core.message.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.utils.desc

class FakeMessageComponent : MessageComponent {

    override val visibleMessage = MutableStateFlow(
        Message(
            text = "Message".desc()
        )
    )

    override fun onActionClick() = Unit
}
