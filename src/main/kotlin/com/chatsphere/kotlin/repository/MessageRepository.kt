package com.chatsphere.kotlin.repository

import com.chatsphere.kotlin.model.Message
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MessageRepository : JpaRepository<Message, UUID> {
    fun findMessageByEmail(email: String): List<Message>
}