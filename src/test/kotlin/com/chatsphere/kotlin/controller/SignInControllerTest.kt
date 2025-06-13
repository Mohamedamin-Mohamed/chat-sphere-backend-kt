package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.PasswordResetDTO
import com.chatsphere.kotlin.dto.SignInDTO
import com.chatsphere.kotlin.dto.UpdatedPasswordDTO
import com.chatsphere.kotlin.dto.UserDTO
import com.chatsphere.kotlin.exception.OAuthSignInRequiredException
import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.service.CodeGeneratorService
import com.chatsphere.kotlin.service.EmailService
import com.chatsphere.kotlin.service.RedisService
import com.chatsphere.kotlin.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Instant
import kotlin.test.assertEquals

class SignInControllerTest {
    private lateinit var userService: UserService
    private lateinit var codeGeneratorService: CodeGeneratorService
    private lateinit var emailService: EmailService
    private lateinit var redisService: RedisService
    private lateinit var signInController: SignInController

    private val email: String = "test@test.com"
    private val password: String = "encrypted"

    @BeforeEach
    fun setup() {
        userService = mockk()
        codeGeneratorService = mockk()
        emailService = mockk()
        redisService = mockk()
        signInController = SignInController(userService, codeGeneratorService, emailService, redisService)
    }

    @Test
    fun `Sign in with email, OAuthSignInRequiredException being thrown`() {
        val signInDTO = SignInDTO(email, "encrypted")

        every { userService.signInWithEmail(any(SignInDTO::class)) } throws OAuthSignInRequiredException("Account exits sign in with provider")

        val exception = assertThrows(OAuthSignInRequiredException::class.java) {
            signInController.signInWithEmail(signInDTO)
        }
        assertEquals("Account exits sign in with provider", exception.message)
        verify { userService.signInWithEmail(any()) }
    }

    @Test
    fun `Sign in with email returns successful response`() {
        val signInDTO = SignInDTO(email, "encrypted")
        val expectedUserDTO = setUserDTO()

        every { userService.signInWithEmail(any(SignInDTO::class)) } returns expectedUserDTO
        val httpResponse: ResponseEntity<Any> = signInController.signInWithEmail(signInDTO)
        val response = httpResponse.body as Map<String, Any>

        assertEquals(HttpStatus.OK, httpResponse.statusCode)
        assertEquals("Login Successful", response["message"])
        assertEquals(expectedUserDTO, response["user"])
        verify { userService.signInWithEmail(any()) }
    }

    @Test
    fun `Verification code check, code expired`() {
        every { redisService.getVerificationCodeFromCache(any(String::class)) } returns null
        val response = signInController.verifyCode(email, "123")

        assertEquals(HttpStatus.GONE, response.statusCode)
        assertEquals("Code expired, request a new one", response.body)
        verify { redisService.getVerificationCodeFromCache(any()) }
    }

    @Test
    fun `Verification code check, wrong code`() {
        every { redisService.getVerificationCodeFromCache(any(String::class)) } returns "231"
        val response = signInController.verifyCode(email, "123")

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Wrong verification code", response.body)
        verify { redisService.getVerificationCodeFromCache(any()) }
    }

    @Test
    fun `Verification code check, successful`() {
        every { redisService.getVerificationCodeFromCache(any(String::class)) } returns "123"
        val response = signInController.verifyCode(email, "123")

        assertEquals(HttpStatus.OK, response.statusCode)
        response.body?.let { assert(it.startsWith("Verification code verified")) }
        verify { redisService.getVerificationCodeFromCache(any()) }
    }

    @Test
    fun `Generate code, User doesn't exist`() {
        every { userService.existsByEmail(any(String::class)) } returns false

        val response = signInController.generateCode(email)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Email not found", response.body)
        verify { userService.existsByEmail(any()) }
    }

    @Test
    fun `Generate code, verification sending fails`() {
        every { userService.existsByEmail(any(String::class)) } returns true
        every { codeGeneratorService.generateCode() } returns "123"
        every { emailService.sendVerificationEmail(any(String::class), any(String::class)) } returns false

        val response = signInController.generateCode(email)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        response.body?.let { assert(it.startsWith("Failed to send")) }

        verify { userService.existsByEmail(any()) }
        verify { codeGeneratorService.generateCode() }
        verify { emailService.sendVerificationEmail(any(), any()) }
    }

    @Test
    fun `Handle password, Password reset, Password changed`() {
        val payload = resetPasswordPayload()

        every { userService.resetPassword(any<PasswordResetDTO>()) } returns true
        val response = signInController.handlePassword(payload)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        response.body?.startsWith("Password reset")
        verify { userService.resetPassword(any<PasswordResetDTO>()) }
    }

    @Test
    fun `Handle password, Password reset, Password not changed`() {
        val payload = resetPasswordPayload()

        every { userService.resetPassword(any<PasswordResetDTO>()) } returns false
        val response = signInController.handlePassword(payload)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        response.body?.startsWith("Error resetting")
        verify { userService.resetPassword(any<PasswordResetDTO>()) }
    }

    @Test
    fun `Handle password, Password update, Password changed`() {
        val payload = updatePasswordPayload()

        every { userService.resetPassword(any<UpdatedPasswordDTO>()) } returns true

        val response = signInController.handlePassword(payload)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        response.body?.startsWith("Password updated")
        verify { userService.resetPassword(any<UpdatedPasswordDTO>()) }
    }

    @Test
    fun `Handle password, Password update, Password not changed`() {
        val payload = updatePasswordPayload()
        every { userService.resetPassword(any<UpdatedPasswordDTO>()) } returns false

        val response = signInController.handlePassword(payload)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        response.body?.let { assert(it == "Incorrect password provided") }
        verify { userService.resetPassword(any<UpdatedPasswordDTO>()) }
    }

    @Test
    fun `Handle password, throws illegalArgumentException`() {
        val payload = resetPasswordPayload()

        every { userService.resetPassword(any<PasswordResetDTO>()) } throws IllegalArgumentException()

        val response = signInController.handlePassword(payload)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        response.body?.let { assertEquals("Invalid request format", it) }
        verify { userService.resetPassword(any<PasswordResetDTO>()) }
    }

    @Test
    fun `Handle password, throws generic exception`() {
        val payload = updatePasswordPayload()

        every { userService.resetPassword(any<UpdatedPasswordDTO>()) } throws Exception()

        val response = signInController.handlePassword(payload)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        response.body?.startsWith("Something went wrong")
        verify { userService.resetPassword(any<UpdatedPasswordDTO>()) }
    }

    @Test
    fun `Handle password, neither password update nor reset`() {
        val response = signInController.handlePassword(emptyMap())

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid request", response.body)
    }

    private fun setUserDTO(): UserDTO {
        return UserDTO.Builder()
            .email(email)
            .createdAt(Instant.now().toString())
            .name("test")
            .oauthProvider("test")
            .picture("empty")
            .bio("testing")
            .phoneNumber("567")
            .token("authorized")
            .build()
    }

    private fun setUserModel(): User {
        val user = User().apply {
            email = email
            passwd = "encrypted"
            oauthProvider = "test"
            oauthId = "123"
        }

        return user
    }

    private fun resetPasswordPayload(): Map<String, String> =
        mapOf("email" to email, "password" to password)

    private fun updatePasswordPayload(): Map<String, String> =
        mapOf("email" to email, "currentPassword" to password, "newPassword" to password)

}