package com.chatsphere.kotlin.dto

data class SignUpRequestDTO(
    val email: String,
    val password: String,
    val name: String
) {
}