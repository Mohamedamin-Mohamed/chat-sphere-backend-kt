package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.dto.MessageDTO
import com.chatsphere.kotlin.exception.ChatCompletionsNotCreatedException
import com.chatsphere.kotlin.util.HttpResponseUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConversationService(
    @Value("\${openAI.apiKey}") val apiKey: String,
    @Value("\${openAI.chatCompletionsURL}") val url: String,
    @Value("\${openAI.chatCompletionsModel}") val model: String,
    @Value("\${openAI.chatCompletionsMaxTokens}") val maxTokens: String,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ConversationService::class.java)
    }

    fun chatCompletions(messageDTOs: Array<MessageDTO>): String {
        logger.info("Sending request to LLM to generate chat response")
        try {
            val requestBody: MutableMap<String, Any> = mutableMapOf()
            requestBody["model"] = model
            requestBody["messages"] = messageDTOs
            requestBody["max_tokens"] = maxTokens

            val httpResponse = HttpResponseUtil.httpResponse(apiKey, url, requestBody)
            if (httpResponse.statusCode() != 200) {
                throw ChatCompletionsNotCreatedException("Error occurred when completing the chat")
            }
            return httpResponse.body()
        } catch (exp: Exception) {
            logger.error("Something went wrong: $exp")
            throw ChatCompletionsNotCreatedException("Error occurred when completing the chat")

        }
    }
}