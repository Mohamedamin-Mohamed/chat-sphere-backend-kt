package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.properties.OpenAIProperties
import com.chatsphere.kotlin.dto.MessageDTO
import com.chatsphere.kotlin.exception.ChatCompletionsNotCreatedException
import com.chatsphere.kotlin.util.HttpResponseUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ConversationService(
    val openAIProperties: OpenAIProperties
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ConversationService::class.java)
    }

    fun chatCompletions(messageDTOs: Array<MessageDTO>): String {
        logger.info("Sending request to LLM to generate chat response")
        try {
            val requestBody: MutableMap<String, Any> = mutableMapOf()
            requestBody["model"] = openAIProperties.chatCompletionsModel
            requestBody["messages"] = messageDTOs
            requestBody["max_tokens"] = openAIProperties.chatCompletionsMaxTokens

            val httpResponse =
                HttpResponseUtil.httpResponse(openAIProperties.apiKey, openAIProperties.chatCompletionsURL, requestBody)
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