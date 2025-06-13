package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.dto.MessageRequestDTO
import com.chatsphere.kotlin.dto.MessageResponseErrorDTO
import com.chatsphere.kotlin.mapper.ModelMapper
import com.chatsphere.kotlin.model.Message
import com.chatsphere.kotlin.repository.MessageRepository
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val modelMapper: ModelMapper
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)
    }

    @Transactional
    fun saveMessage(messageDTO: MessageRequestDTO): Any {
        try {
            val message = modelMapper.map(messageDTO)
            messageRepository.save(message)

            return modelMapper.map(message)
        } catch (exp: Exception) {
            logger.error("Something went wrong $exp")
            return MessageResponseErrorDTO(
                false,
                "Couldn't save ${messageDTO.sender} with sender of email ${messageDTO.email}"
            )
        }
    }

    fun loadMessages(email: String): List<Message> {
        return messageRepository.findMessageByEmail(email)
    }
}