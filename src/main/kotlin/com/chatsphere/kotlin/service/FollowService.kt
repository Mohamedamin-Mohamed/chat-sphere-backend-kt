package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.dto.FollowFollowingRequest
import com.chatsphere.kotlin.exception.EmailNotFoundException
import com.chatsphere.kotlin.mapper.ModelMapper
import com.chatsphere.kotlin.model.Follow
import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.repository.FollowRepository
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
class FollowService(
    private val modelMapper: ModelMapper,
    private val followRepository: FollowRepository,
    private val userRelationshipService: UserRelationshipService
) {
    @Transactional
    fun followUser(followFollowingRequest: FollowFollowingRequest): Unit {
        val followerEmail = followFollowingRequest.followerEmail
        val followingEmail = followFollowingRequest.followingEmail

        val follower = userRelationshipService.findByEmail(followerEmail)
            ?: throw EmailNotFoundException("Follower email not found")
        val following = userRelationshipService.findByEmail(followingEmail)
            ?: throw EmailNotFoundException("Following email not found")

        val follow = modelMapper.map(follower, following)
        followRepository.save(follow)
    }

    @Transactional
    fun unfollowUser(followFollowingRequest: FollowFollowingRequest): Unit {
        val followerEmail = followFollowingRequest.followerEmail
        val follower = userRelationshipService.findByEmail(followerEmail)
            ?: throw EmailNotFoundException("Follower email not found")
        deleteFollow(follower)
    }

    @Transactional
    fun deleteFollow(follower: User): Unit {
        val follows = followRepository.findFollowsByFollower(follower)
        followRepository.deleteAll(follows)
    }

    fun findByFollower(requester: User): List<Follow> {
        return followRepository.findByFollower(requester)
    }
}