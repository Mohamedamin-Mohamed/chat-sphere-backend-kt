package com.chatsphere.kotlin.dto

class MessageResponseDTO private constructor(
    val messageId: String?,
    val email: String?,
    val sender: String?,
    val message: String?,
    val timestamp: String?
) {
    class Builder {
        private var messageId: String? = null
        private var email: String? = null
        private var sender: String? = null
        private var message: String? = null
        private var timestamp: String? = null

        fun messageId(messageId: String?): Builder = apply { this.messageId = messageId }
        fun email(email: String?): Builder = apply { this.email = email }
        fun sender(sender: String?): Builder = apply { this.sender = sender }
        fun message(message: String?): Builder = apply { this.message = message }
        fun timestamp(timestamp: String?): Builder = apply { this.timestamp = timestamp }

        fun build(): MessageResponseDTO = MessageResponseDTO(messageId, email, sender, message, timestamp)
    }
}