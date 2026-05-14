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
    private val _all = mutableListOf<Message>()

    val all: List<Message> get() = _all
    val last: Message? get() = _all.lastOrNull()
    val first: Message? get() = _all.firstOrNull()
    val isEmpty: Boolean get() = _all.isEmpty()

    override val messageFlow: Flow<Message> = _messageFlow

    override fun showMessage(message: Message) {
        _all += message
        _messageFlow.tryEmit(message)
    }
}