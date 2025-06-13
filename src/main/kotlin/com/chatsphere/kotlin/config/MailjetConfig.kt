package com.chatsphere.kotlin.config

import com.mailjet.client.ClientOptions
import com.mailjet.client.MailjetClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class MailjetConfig(
    @Value("\${mailjet.apiKey}") private val apiKey: String,
    @Value("\${mailjet.apiSecretKey}") private val apiSecretKey: String
) {
    fun connect(): MailjetClient {
        return MailjetClient(
            ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecretKey)
                .build()
        )

    }
}