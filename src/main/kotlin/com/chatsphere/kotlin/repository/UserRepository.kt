package com.chatsphere.kotlin.repository

import com.chatsphere.kotlin.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?
    fun findByOauthId(oauthId: String): User

    /*here we are selecting users with pattern matching and case-insensitive, where the query appears anywhere in the email or name and is not the same profile as
    the requesters email */
    @Query("select user from User user where (lower(user.name) like lower(concat('%', :query, '%')) or lower(user.email) like lower(concat('%', :query, '%'))) and user.email <> :email")
    fun searchUsersByNameOrEmail(@Param("query") query: String, @Param("email") email: String): List<User>

}