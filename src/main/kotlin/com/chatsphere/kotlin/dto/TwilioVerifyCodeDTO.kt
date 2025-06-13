package com.chatsphere.kotlin.dto

data class TwilioVerifyCodeDTO(val email: String, val phoneNumber: String, var code: String) {
}