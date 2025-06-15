package com.chatsphere.kotlin.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "opensearch")
data class OpenSearchProperties(
    val indexName: String = "",
    val url: String = "",
    val userName: String = "",
    val password: String = "",
    val port: Int = 443,
    val protocol: String = "",
    val searchField: String = "",
) {
}