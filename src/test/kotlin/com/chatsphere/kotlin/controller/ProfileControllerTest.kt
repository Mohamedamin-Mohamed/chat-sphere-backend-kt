package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.UpdateProfileDTO
import com.chatsphere.kotlin.dto.UserDTO
import com.chatsphere.kotlin.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import kotlin.test.assertEquals

class ProfileControllerTest {
    private lateinit var userService: UserService
    private lateinit var profileController: ProfileController
    private val email = "test@test.com"
    private val name = "Test"
    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        userService = mockk()
        profileController = ProfileController(userService)
    }

    @Test
    fun `Update profile, Profile updated`() {
        val updateProfileDTO = generateUpdateProfileDTO()
        val mockFile = generateMockFile()
        val expectedUserDTO = generateUserDTO()
        every { userService.updateProfile(any<UpdateProfileDTO>(), any<MultipartFile>()) } returns expectedUserDTO

        val response = profileController.updateProfile(updateProfileDTO, mockFile)
        val body = response.body as Map<*, *>
        val userDTO = body["user"] as UserDTO
        val message: String = body["message"] as String

        assertEquals(HttpStatus.OK, response.statusCode)
        assert(message.startsWith("Profile has been"))
        assertEquals(expectedUserDTO, userDTO)
        assertEquals(now.toString(), userDTO.createdAt)
        verify { userService.updateProfile(any<UpdateProfileDTO>(), any<MultipartFile>()) }
    }

    @Test
    fun `Update profile, Profile not updated`() {
        val updateProfileDTO = generateUpdateProfileDTO()
        every { userService.updateProfile(any<UpdateProfileDTO>(), any<MultipartFile>()) } returns null

        val response = profileController.updateProfile(updateProfileDTO, null)
        val body = response.body as Map<*, *>
        val userDTO = body["user"]
        val message = body["message"] as String?
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(null, userDTO)
        message?.let { assert(it == "Profile update failed") }
    }

    private fun generateUpdateProfileDTO(): UpdateProfileDTO {
        return UpdateProfileDTO(email, email, name, "", "123")

    }

    private fun generateUserDTO(): UserDTO {
        return UserDTO
            .Builder()
            .email(email)
            .name(name)
            .createdAt(now.toString())
            .build()
    }

    private fun generateMockFile(): MockMultipartFile {
        val content = "Hello, file".toByteArray()
        return MockMultipartFile("profilePictureDetails", "test.txt", "text/plain", content)
    }

}