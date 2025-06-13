package com.chatsphere.kotlin.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


class UpdateProfileDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Valid email is required")
    val email: String = "",

    @field:Email(message = "New email must be valid if provided")
    val newEmail: String = "",
    val name: String = "",
    val bio: String = "",
    var phoneNumber: String = ""
) {
}