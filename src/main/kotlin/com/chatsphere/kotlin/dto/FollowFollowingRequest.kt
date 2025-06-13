package com.chatsphere.kotlin.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class FollowFollowingRequest(
    @field:NotBlank(message = "Email is required") @Email(message = "Follower email need sto be valid if provided") val followerEmail: String,
    @field:NotBlank(message = "Email is required") @Email(message = "Following email need sto be valid if provided") val followingEmail: String
) {
}