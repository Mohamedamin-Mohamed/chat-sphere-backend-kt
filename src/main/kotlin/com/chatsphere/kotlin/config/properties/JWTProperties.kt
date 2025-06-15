package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "security.jwt")
data class JWTProperties(
    val secretKey: String? = null,
    val issuer: String? = null,
) {

}