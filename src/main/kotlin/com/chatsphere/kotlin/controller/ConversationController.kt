package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.MessageDTO
import com.chatsphere.kotlin.service.ConversationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/chat")
class ConversationController(
    private val conversationService: ConversationService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ConversationController::class.java)
    }

    @PostMapping("/completions")
    fun askChatGPT(@RequestBody messageDTOs: Array<MessageDTO>): ResponseEntity<Any> {
        logger.info("Performing chat completion")
        val obj = conversationService.chatCompletions(messageDTOs)
        return ResponseEntity.ok(obj)
    }
}