package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aws")
data class AWSProperties(
    val accessKeyId: String? = null,
    val secretAccessKey: String? = null,
    val s3BucketName: String = "",
    val s3BucketKeyName: String = "",
    val region: String = "us-east-2"
) {
}