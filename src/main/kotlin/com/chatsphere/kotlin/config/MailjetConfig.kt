package com.chatsphere.kotlin.config

import com.chatsphere.kotlin.config.properties.MailjetProperties
import com.mailjet.client.ClientOptions
import com.mailjet.client.MailjetClient
import org.springframework.context.annotation.Configuration

@Configuration
class MailjetConfig(
    private val mailjetProperties: MailjetProperties
) {
    fun connect(): MailjetClient {
        return MailjetClient(
            ClientOptions.builder()
                .apiKey(mailjetProperties.apiKey)
                .apiSecretKey(mailjetProperties.apiSecretKey)
                .build()
        )

    }
}