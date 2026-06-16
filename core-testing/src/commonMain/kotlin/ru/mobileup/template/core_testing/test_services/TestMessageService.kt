package ru.mobileup.template.core_testing.test_services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message

/**
 * Test implementation of [ru.mobileup.template.core.message.data.MessageService].
 *
 * Stores emitted messages for assertions.
 */
class TestMessageService : MessageService {
    private val _messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 16)
    private val _messages = mutableListOf<Message>()

    val messages: List<Message> get() = _messages
    val lastMessage: Message? get() = _messages.lastOrNull()
    val firstMessage: Message? get() = _messages.firstOrNull()
    val wasNoMessages: Boolean get() = _messages.isEmpty()

    override val messageFlow: Flow<Message> = _messageFlow

    override fun showMessage(message: Message) {
        _messages += message
        _messageFlow.tryEmit(message)
    }
}