package com.chatsphere.kotlin.dto

data class UpdatedPasswordDTO(val email: String, val currentPassword: String, val newPassword: String) {
}