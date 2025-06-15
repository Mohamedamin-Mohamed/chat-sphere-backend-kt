package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("mailjet")
data class EmailProperties(
    val senderEmail: String = "",
    val senderName: String = "",
    val fileName: String = "",
) {
}