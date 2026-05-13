package ru.mobileup.template.core_testing.message.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message

/**
 * Test implementation of [MessageService].
 *
 * Stores emitted messages for direct assertions and also exposes a hot flow
 * to support stream-based checks when needed.
 */
class TestMessageService : MessageService {
    private val _messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 16)
    private val _all = mutableListOf<Message>()

    /**
     * All emitted messages in chronological order.
     */
    val all: List<Message> get() = _all

    /**
     * Last emitted message or `null` when no messages were sent.
     */
    val last: Message? get() = _all.lastOrNull()

    /**
     * First emitted message or `null` when no messages were sent.
     */
    val first: Message? get() = _all.firstOrNull()

    /**
     * `true` when no messages have been emitted.
     */
    val isEmpty: Boolean get() = _all.isEmpty()

    override val messageFlow: Flow<Message> = _messageFlow

    /**
     * Emits a message in a non-suspending way for deterministic tests.
     */
    override fun showMessage(message: Message) {
        _all += message
        _messageFlow.tryEmit(message)
    }
}
