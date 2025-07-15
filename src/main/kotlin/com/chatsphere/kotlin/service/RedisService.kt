package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.RedisConfig
import com.chatsphere.kotlin.config.properties.RedisProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis
import redis.clients.jedis.params.SetParams

@Service
class RedisService(
    private val redisConfig: RedisConfig,
    private val redisProperties: RedisProperties
) {
    val logger: Logger = LoggerFactory.getLogger(RedisService::class.java)

    fun addVerificationCodeToCache(email: String, code: String): Boolean {
        logger.info("Adding verification code $code for $email to cache")
        return try {
            val jedis: Jedis = redisConfig.connect()
            val cacheTTLSecond = 600L
            val setParams = SetParams().ex(cacheTTLSecond)
            val response = jedis.set(redisProperties.verificationKey + email, code, setParams)
            response.equals("OK")
        } catch (exp: Exception) {
            logger.error("Failed to add verification code to Redis: ${exp.message}")
            throw RuntimeException(exp)
        }
    }

    fun getVerificationCodeFromCache(email: String): String? {
        try {
            val jedis = redisConfig.connect()
            return jedis.get(redisProperties.verificationKey + email)?.takeIf { it.isNotEmpty() }
        } catch (exp: Exception) {
            logger.error("Failed to retrieve verification code from Redis: ${exp.message}")

            throw RuntimeException(exp)
        }
    }
}