package com.chatsphere.kotlin.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.Jedis

@Configuration
class RedisConfig(
    @Value("\${redis.redisHost}") val redisHost: String,
    @Value("\${redis.redisPassword}") val redisPassword: String,
    @Value("\${redis.redisPort}") val port: Int
) {
    fun connect(): Jedis {
        val jedis = Jedis(redisHost, port, true)
        if (redisPassword.isNotBlank()) {
            jedis.auth(redisPassword)
        }
        jedis.auth(redisPassword)
        return jedis
    }
}