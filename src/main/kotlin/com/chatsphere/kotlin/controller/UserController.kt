package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.SearchRequest
import com.chatsphere.kotlin.dto.UserSearchDTO
import com.chatsphere.kotlin.dto.UserStatsDTO
import com.chatsphere.kotlin.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
class UserController(
    private val userService: UserService,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/search")
    fun searchUsers(@RequestParam requesterEmail: String, @RequestParam query: String): ResponseEntity<Any> {
        logger.info("Searching for users with the query $query requested by $requesterEmail")
        val userSearchDtoList: List<UserSearchDTO> = userService.searchUsers(SearchRequest(requesterEmail, query))

        return ResponseEntity.ok(userSearchDtoList)
    }

    @GetMapping("/stats")
    fun getUserStats(@RequestParam email: String): ResponseEntity<Any> {
        logger.info("Received request to get stats for user with email $email")
        val userStatsDTO: UserStatsDTO = userService.getUserStats(email)

        return ResponseEntity.ok(userStatsDTO)
    }
}