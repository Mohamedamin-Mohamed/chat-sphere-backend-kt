package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "filenames")
data class FileNamesProperties(
    val verificationCode: String = ""
) {
}