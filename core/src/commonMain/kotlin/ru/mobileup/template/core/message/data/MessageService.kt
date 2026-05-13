package ru.mobileup.template.core.message.data

import kotlinx.coroutines.flow.Flow
import ru.mobileup.template.core.message.domain.Message

/**
 * A service for centralized message showing
 */
interface MessageService {

    val messageFlow: Flow<Message>

    fun showMessage(message: Message)
}