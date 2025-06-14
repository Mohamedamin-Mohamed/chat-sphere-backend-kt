package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.UpdateProfileDTO
import com.chatsphere.kotlin.dto.UserDTO
import com.chatsphere.kotlin.service.UserService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/profile")
@Validated
class ProfileController(
    private val userService: UserService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ProfileController::class.java)
    }

    @PostMapping("/update")
    fun updateProfile(
        @ModelAttribute @Valid updateProfileDTO: UpdateProfileDTO,
        @RequestPart(value = "profilePictureDetails", required = false) multipartFile: MultipartFile?
    ): ResponseEntity<Any> {
        logger.info("Updating profile for ${updateProfileDTO.email}")

        val userDTO: UserDTO? = userService.updateProfile(updateProfileDTO, multipartFile)
        val profileUpdated = userDTO != null

        val response: MutableMap<String, Any?> = mutableMapOf()
        response["user"] = userDTO
        response["message"] = if (profileUpdated) "Profile has been updated successfully" else "Profile update failed"

        return ResponseEntity(response, if (profileUpdated) HttpStatus.OK else HttpStatus.INTERNAL_SERVER_ERROR)
    }
}