package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.PasswordResetDTO
import com.chatsphere.kotlin.dto.SignInDTO
import com.chatsphere.kotlin.dto.UpdatedPasswordDTO
import com.chatsphere.kotlin.service.CodeGeneratorService
import com.chatsphere.kotlin.service.EmailService
import com.chatsphere.kotlin.service.RedisService
import com.chatsphere.kotlin.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("auth/signin/")
class SignInController(
    private val userService: UserService,
    private val codeGeneratorService: CodeGeneratorService,
    private val emailService: EmailService,
    private val redisService: RedisService,
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SignInController::class.java)
    }

    @PostMapping("")
    fun signInWithEmail(@RequestBody signInDto: SignInDTO): ResponseEntity<Any> {
        logger.info("Received request for $signInDto.email to sign in")
        val userDto = userService.signInWithEmail(signInDto)
        val response = hashMapOf<String, Any>()
        response["user"] = userDto
        response["message"] = "Login Successful"
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("verify")
    fun verifyCode(@RequestParam email: String, @RequestParam code: String): ResponseEntity<String> {
        logger.info("Received request to verify code $code for $email")
        val cacheVerificationCode: String = redisService.getVerificationCodeFromCache(email)
            ?: return ResponseEntity("Code expired, request a new one", HttpStatus.GONE)

        if (cacheVerificationCode == code) {
            return ResponseEntity("Verification code verified, reset password", HttpStatus.OK)
        }

        return ResponseEntity("Wrong verification code", HttpStatus.CONFLICT)
    }

    @GetMapping("email_lookup/generate_code")
    fun generateCode(@RequestParam email: String): ResponseEntity<String> {
        logger.info("Received request to generate code for $email")

        if (!userService.existsByEmail(email)) {
            return ResponseEntity("Email not found", HttpStatus.NOT_FOUND)
        }

        val code = codeGeneratorService.generateCode()

        if (!emailService.sendVerificationEmail(email, code)) {
            return ResponseEntity("Failed to send verification code", HttpStatus.INTERNAL_SERVER_ERROR)
        }

        if (!redisService.addVerificationCodeToCache(email, code)) {
            return ResponseEntity("Failed to store code in cache", HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return ResponseEntity("Verification code sent successfully", HttpStatus.CREATED)
    }

    @PostMapping("password/reset")
    fun handlePassword(@RequestBody passwordDto: Map<String, String>): ResponseEntity<String> {
        logger.info("Received request to handle password reset $passwordDto")
        val mapper = jacksonObjectMapper()

        return try {
            val passwordMap: Map<*, *> = mapper.convertValue(passwordDto, Map::class.java)

            if (passwordMap.containsKey("email") && passwordMap.containsKey("password")) {
                val passwordResetDto = mapper.convertValue(passwordDto, PasswordResetDTO::class.java)
                logger.info("Resetting password for ${passwordResetDto.email}")

                val passwordChanged: Boolean = userService.resetPassword(passwordResetDto)

                return if (passwordChanged) {
                    return ResponseEntity("Password reset successfully", HttpStatus.CREATED)
                } else {
                    ResponseEntity("Error resetting password", HttpStatus.INTERNAL_SERVER_ERROR)
                }
            } else if (passwordMap.containsKey("currentPassword") && passwordMap.containsKey("newPassword")) {
                val updatedPasswordDTO: UpdatedPasswordDTO =
                    mapper.convertValue(passwordDto, UpdatedPasswordDTO::class.java)
                logger.info("Received request to update password for ${updatedPasswordDTO.email}")
                val passwordChanged: Boolean = userService.resetPassword(updatedPasswordDTO)

                return if (passwordChanged) {
                    ResponseEntity("Password updated successfully", HttpStatus.CREATED)
                } else {
                    ResponseEntity("Incorrect password provided", HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }

            return ResponseEntity("Invalid request", HttpStatus.BAD_REQUEST)
        } catch (exp: IllegalArgumentException) {
            logger.error("Error converting request payload $exp")
            ResponseEntity.badRequest().body("Invalid request format")
        } catch (exp: Exception) {

            ResponseEntity.internalServerError().body("Something went wrong ${exp.message}")
        }

    }

}