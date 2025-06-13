package com.chatsphere.kotlin.dto

class UserDTO private constructor(
    val email: String,
    val createdAt: String,
    val name: String,
    val oauthProvider: String,
    val picture: String,
    val bio: String,
    val phoneNumber: String,
    val token: String
) {
    class Builder {
        private var email: String = ""
        private var createdAt: String = ""
        private var name: String = ""
        private var oauthProvider: String = ""
        private var picture: String = ""
        private var bio: String = ""
        private var phoneNumber: String = ""
        private var token: String = ""

        fun email(email: String) = apply { this.email = email }
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun name(name: String) = apply { this.name = name }
        fun oauthProvider(oauthProvider: String) = apply { this.oauthProvider = oauthProvider }
        fun picture(picture: String) = apply { this.picture = picture }
        fun bio(bio: String) = apply { this.bio = bio }
        fun phoneNumber(phoneNumber: String) = apply { this.phoneNumber = phoneNumber }
        fun token(token: String) = apply { this.token = token }
        fun build(): UserDTO = UserDTO(email, createdAt, name, oauthProvider, picture, bio, phoneNumber, token)
    }
}