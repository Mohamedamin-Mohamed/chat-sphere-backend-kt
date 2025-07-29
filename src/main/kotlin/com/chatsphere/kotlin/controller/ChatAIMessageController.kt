package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.MessageRequestDTO
import com.chatsphere.kotlin.model.Message
import com.chatsphere.kotlin.service.MessageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/message")
class ChatMessageController(
    private val messageService: MessageService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ChatMessageController::class.java)
    }

    @PostMapping
    fun saveMessage(@RequestBody messageDTO: MessageRequestDTO): ResponseEntity<Any> {
        logger.info("Saving new message from sender ${messageDTO.sender} of length ${messageDTO.message.length}")

        val response = messageService.saveMessage(messageDTO)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    fun loadMessages(@RequestParam email: String): ResponseEntity<List<Message>> {
        logger.info("Loading messages for $email")

        val messages: List<Message> = messageService.loadMessages(email)
        return ResponseEntity(messages, HttpStatus.OK)
    }
}