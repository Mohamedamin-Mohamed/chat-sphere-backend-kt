package com.chatsphere.kotlin.config

import com.twilio.Twilio
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class TwilioConfig(
    @Value("\${twilio.accountSID}") private val accountSID: String,
    @Value("\${twilio.authToken}") private val authToken: String,
) {
    fun connect() {
        Twilio.init(accountSID, authToken)
    }
}