package ru.mobileup.template.core.message.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.core.message.domain.Message

class FakeMessageComponent(
    initialMessage: Message? = null
) : MessageComponent {

    override val visibleMessage = MutableStateFlow(initialMessage)

    override fun onActionClick() = Unit
}
