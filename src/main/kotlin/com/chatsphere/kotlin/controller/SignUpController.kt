package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.OAuthSignUpRequestDTO
import com.chatsphere.kotlin.dto.SignUpRequestDTO
import com.chatsphere.kotlin.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth/signup")
class SignUpController(
    private val userService: UserService,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(SignInController::class.java)
    }

    @PostMapping("/email")
    fun signUpWithEmail(signUpRequestDTO: SignUpRequestDTO): ResponseEntity<Any> {
        logger.info("Received request for ${signUpRequestDTO.email} to sign up with email")
        val userDTO = userService.signUpWithEmail(signUpRequestDTO)

        val response: Map<String, Any> = mapOf("user" to userDTO, "message" to "Account created Successful")
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @PostMapping("oauth")
    fun signUpWithOauth(oAuthSignUpRequest: OAuthSignUpRequestDTO): ResponseEntity<Any> {
        logger.info("Received request from ${oAuthSignUpRequest.email} to sign up with oauth provider ${oAuthSignUpRequest.oauthProvider}")
        if (oAuthSignUpRequest.oauthProvider == "Apple") {
            /*
             * if this is true it means it not the first time using is signing in with Apple so apple won't return users
             * email again on subsequent logins. In this case link the stored user oauth id with the request's oauth id.
             */
            if (oAuthSignUpRequest.email.isEmpty()) {
                val user = userService.findByOauthId(oAuthSignUpRequest.oauthId)
                logger.info("Linking $user.name apple account with oauth id")
                oAuthSignUpRequest.email = user.email.toString()
                oAuthSignUpRequest.name = user.name.toString()
            }
        }

        val userDTO = userService.signUpWithAuth(oAuthSignUpRequest)

        val response: MutableMap<String, Any> = hashMapOf()
        response["user"] = userDTO
        response["message"] = "Account created Successful"
        return ResponseEntity(response, HttpStatus.CREATED)
    }


}