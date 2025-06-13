package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.FollowFollowingRequest
import com.chatsphere.kotlin.service.FollowService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/follow")
class FollowController(
    private val followService: FollowService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(FollowController::class.java)
    }

    @PostMapping("/add")
    fun followUser(@RequestBody @Valid followFollowingRequest: FollowFollowingRequest): ResponseEntity<Any> {
        logger.info("Received follow request form ${followFollowingRequest.followerEmail} to follow ${followFollowingRequest.followingEmail}")

        followService.followUser(followFollowingRequest)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/remove")
    fun unfollowUser(@RequestBody @Valid followFollowingRequest: FollowFollowingRequest): ResponseEntity<Any> {
        logger.info("Received unfollow request from ${followFollowingRequest.followerEmail} to unfollow ${followFollowingRequest.followingEmail}")

        followService.unfollowUser(followFollowingRequest)
        return ResponseEntity.ok().build()
    }
}