package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserRelationshipService(
     private val userRepository: UserRepository
) {
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}