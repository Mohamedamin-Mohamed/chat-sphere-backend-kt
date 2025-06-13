package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.service.JwtAuthService
import com.chatsphere.kotlin.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth/refresh-token")
class TokenController(
    private val jwtAuthService: JwtAuthService,
    private val userService: UserService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(TokenController::class.java)
    }

    @GetMapping("/{email}")
    fun refreshToken(@PathVariable email: String): ResponseEntity<Any> {
        logger.info("Performing refresh token for user $email")
        val user = userService.findRawUserEmail(email)
        val token = jwtAuthService.createToken(user)

        return ResponseEntity(hashMapOf("token" to token), HttpStatus.OK)
    }
}