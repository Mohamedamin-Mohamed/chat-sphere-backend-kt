package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "r")
data class RedisProperties(
    val host: String = "",
    val password: String = "",
    val port: Int = 6379,
    val verificationKey: String = "",
) {
}