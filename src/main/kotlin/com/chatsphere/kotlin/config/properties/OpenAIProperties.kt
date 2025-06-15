package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "openai")
data class OpenAIProperties(
    val apiKey: String = "",
    val embeddingURL: String = "",
    val embeddingModel: String = "",
    val chatCompletionsURL: String = "",
    val chatCompletionsModel: String = "",
    val chatCompletionsMaxTokens: Int = 100,
) {
}