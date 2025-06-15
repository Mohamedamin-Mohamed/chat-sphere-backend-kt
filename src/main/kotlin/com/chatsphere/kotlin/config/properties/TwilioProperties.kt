package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "twilio")
data class TwilioProperties(
    val accountSID: String? = null,
    val authToken: String? = null,
    val verificationSID: String? = null,
    val statusPending: String = "",
    val statusApproved: String = "",
) {
}