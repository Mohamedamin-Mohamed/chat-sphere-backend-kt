package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.SignUpRequestDTO
import com.chatsphere.kotlin.dto.UserDTO
import com.chatsphere.kotlin.service.UserService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.opensearch.client.opensearch.indices.IndexState
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class SignUpControllerTest {
    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var signUpController: SignUpController

    private val email = "test@test.com"
    private val name = "test"
    private val now = Instant.now()

    @Test
    fun signUpWithEmail() {
        val userDTO = generateUserDTO()
        every { userService.signUpWithEmail(any<SignUpRequestDTO>()) } returns userDTO
    }

    @Test
    fun signUpWithOauth() {
    }

    fun generateUserDTO(): UserDTO {
        return UserDTO
            .Builder()
            .email(email)
            .name(name)
            .createdAt(now.toString())
            .build()
    }

}