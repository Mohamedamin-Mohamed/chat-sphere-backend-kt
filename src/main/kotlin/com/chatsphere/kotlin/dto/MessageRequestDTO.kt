package com.chatsphere.kotlin.dto

data class MessageRequestDTO(
    val email: String,
    val sender: String,
    val message: String,
    val timestamp: String
) {
}