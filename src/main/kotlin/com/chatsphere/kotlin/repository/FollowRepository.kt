package com.chatsphere.kotlin.repository

import com.chatsphere.kotlin.model.Follow
import com.chatsphere.kotlin.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<Follow, Long>{
    fun findFollowsByFollower(user: User): List<Follow>
    fun findByFollower(user: User): List<Follow>
}