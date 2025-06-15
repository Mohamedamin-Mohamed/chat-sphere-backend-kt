package com.chatsphere.kotlin.config

import com.chatsphere.kotlin.config.properties.RedisProperties
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.Jedis

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {
    val redisPassword = redisProperties.password
    fun connect(): Jedis {
        val jedis = Jedis(redisProperties.host, redisProperties.port, true)
        if (redisPassword.isNotBlank()) {
            jedis.auth(redisPassword)
        }
        jedis.auth(redisPassword)
        return jedis
    }
}