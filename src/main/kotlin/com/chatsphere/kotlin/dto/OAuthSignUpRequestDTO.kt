package com.chatsphere.kotlin.dto

data class OAuthSignUpRequestDTO(
    var email: String,
    var name: String,
    val oauthProvider: String,
    val oauthId: String,
    val emailVerified: Boolean,
    val picture: String?,
    var authorizationCode: String?,
    val identityToken: String?,
    val accessToken: String?
) {
}