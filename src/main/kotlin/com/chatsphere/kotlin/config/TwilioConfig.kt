package com.chatsphere.kotlin.config

import com.chatsphere.kotlin.config.properties.TwilioProperties
import com.twilio.Twilio
import org.springframework.context.annotation.Configuration

@Configuration
class TwilioConfig(
    private val twilioProperties: TwilioProperties
) {
    fun connect() {
        Twilio.init(twilioProperties.accountSID, twilioProperties.authToken)
    }
}