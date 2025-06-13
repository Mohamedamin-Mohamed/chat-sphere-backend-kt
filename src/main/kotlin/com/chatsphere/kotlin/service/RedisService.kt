package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.RedisConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis
import redis.clients.jedis.params.SetParams

@Service
class RedisService(
    private val redisConfig: RedisConfig,
    @Value("\${redis.redisVerificationKey}") val redisKey: String
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(RedisService::class.java)
    }

    fun addVerificationCodeToCache(email: String, code: String): Boolean {
        logger.info("Adding verification code $code for $email to cache")
        return try {
            val jedis: Jedis = redisConfig.connect()
            val cacheTTLSecond = 600L
            val setParams = SetParams().ex(cacheTTLSecond)
            val response = jedis.set(redisKey + email, code, setParams)
            response.equals("OK")
        } catch (exp: Exception) {
            throw RuntimeException(exp)
        }
    }

    fun getVerificationCodeFromCache(email: String): String? {
        try {
            val jedis = redisConfig.connect()
            return jedis.get(redisKey + email)?.takeIf { it.isNotEmpty() }
        } catch (exp: Exception) {
            throw RuntimeException(exp)
        }
    }
}