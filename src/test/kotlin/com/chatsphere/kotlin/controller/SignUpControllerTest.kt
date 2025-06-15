package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.OAuthSignUpRequestDTO
import com.chatsphere.kotlin.dto.SignUpRequestDTO
import com.chatsphere.kotlin.dto.UserDTO
import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.Instant

class SignUpControllerTest {
    private lateinit var userService: UserService
    private lateinit var signUpController: SignUpController

    private val email = "test@test.com"
    private val name = "test"
    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        userService = mockk()
        signUpController = SignUpController(userService)
    }

    @Test
    fun signUpWithEmail() {
        val userDTO = generateUserDTO()
        val signUpRequestDTO = generateSignUpRequestDTO()
        every { userService.signUpWithEmail(any<SignUpRequestDTO>()) } returns userDTO

        val response = signUpController.signUpWithEmail(signUpRequestDTO)
        val body = response.body as? Map<*, *>
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assert(body?.get("message") == "Account created Successful")
        assert(body?.get("user") == userDTO)
    }

    @Test
    fun signUpWithOauth() {
        val oAuthSignUpRequestDTO = generateOAuthSignUpRequestDTO()
        val userDTO = generateUserDTO()
        every { userService.signUpWithAuth(any<OAuthSignUpRequestDTO>()) } returns userDTO

        val response = signUpController.signUpWithOauth(oAuthSignUpRequestDTO)
        val body = response.body as? Map<*, *>
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assert(body?.get("message") == "Account created Successful")
        assert(body?.get("user") == userDTO)
    }

    @Test
    fun `Sign up with Oauth, Apple Provider, not first time signing up`() {
        val oAuthSignUpRequestDTO = generateOAuthSignUpRequestDTO(true)
        oAuthSignUpRequestDTO.email = ""
        val user = generateUser()
        val userDTO = generateUserDTO()

        every { userService.findByOauthId(any<String>()) } returns user
        every { userService.signUpWithAuth(any<OAuthSignUpRequestDTO>()) } returns userDTO

        val response = signUpController.signUpWithOauth(oAuthSignUpRequestDTO)
        val body = response.body as? Map<*, *>
        val returnedUserDTO: UserDTO = body?.get("user") as UserDTO

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(body["message"], "Account created Successful")
        assertEquals(userDTO, body["user"])
        assertEquals(now.toString(), userDTO.createdAt)
        assertEquals(email, returnedUserDTO.email)
        assertEquals(name, returnedUserDTO.name)
    }

    private fun generateSignUpRequestDTO(): SignUpRequestDTO {
        return SignUpRequestDTO(email, "encrypted", name)
    }

    private fun generateUserDTO(): UserDTO {
        return UserDTO
            .Builder()
            .email(email)
            .name(name)
            .createdAt(now.toString())
            .build()
    }

    private fun generateOAuthSignUpRequestDTO(provider: Boolean = false): OAuthSignUpRequestDTO {
        return OAuthSignUpRequestDTO(
            email,
            name,
            if (provider) "Apple" else "Test",
            "123",
            true,
            null,
            null,
            null,
            null
        )
    }

    private fun generateUser(): User {
        val user = User()
        user.email = email
        user.name = name
        return user
    }

}