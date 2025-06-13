package com.chatsphere.kotlin.mapper

import com.chatsphere.kotlin.dto.UserDTO
import com.chatsphere.kotlin.dto.UserSearchDTO
import com.chatsphere.kotlin.dto.UserSearchResponse
import com.chatsphere.kotlin.exception.EmailNotFoundException
import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.service.FollowService
import com.chatsphere.kotlin.service.UserRelationshipService
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.function.BiFunction

@Service
class UserSearchDTOMapper(
    private val userRelationshipService: UserRelationshipService,
    private val followService: FollowService,
    private val modelMapper: ModelMapper
) : BiFunction<User, String, UserSearchDTO> {
    override fun apply(targetUser: User, requesterEmail: String): UserSearchDTO {
        val requesterFollowsUser = followsUser(requesterEmail, targetUser.id)
        val requesterIsFollowed = isUserFollowingRequester(requesterEmail, targetUser)
        val mutualFriendsList = getMutualFriends(targetUser, requesterEmail)

        val topThreeMutualFriends: List<UserDTO> = mutualFriendsList.map { modelMapper.map(it) }.take(3)
        val topThreeMutualFriendsSearchDTOList: List<UserSearchDTO> =
            topThreeMutualFriends.map { convertUserToUserSearchDTO(it, requesterEmail) }

        val userSearchResponse = UserSearchResponse.Builder()
            .user(targetUser)
            .mutualFriendsList(mutualFriendsList)
            .topThreeMutualFriendsSearchDTOList(topThreeMutualFriendsSearchDTOList)
            .requesterFollowsUser(requesterFollowsUser)
            .requesterIsFollowed(requesterIsFollowed)
            .build()

        return modelMapper.map(userSearchResponse)
    }

    private fun isUserFollowingRequester(targetUserEmail: String, user: User): Boolean {
        val followingUser = userRelationshipService.findByEmail(targetUserEmail)
            ?: throw EmailNotFoundException("Following email not found")

        return followsUser(user.email ?: "", followingUser.id)
    }

    private fun followsUser(requesterEmail: String, targetUserId: Long): Boolean {
        val requester = userRelationshipService.findByEmail(requesterEmail)
            ?: throw EmailNotFoundException("Requester email not found")
        val followList = followService.findByFollower(requester)
        return followList.any { it.following?.id == targetUserId }
    }

    private fun getMutualFriends(targetUser: User, requesterEmail: String): List<User> {
        val requesterUser = userRelationshipService.findByEmail(requesterEmail)
            ?: throw EmailNotFoundException("Requester user email not found")

        val requesterFollowing = followService.findByFollower(requesterUser).mapNotNull { it.following }
        val targetFollowing = followService.findByFollower(targetUser).mapNotNull { it.following }

        return requesterFollowing.intersect(targetFollowing.toSet()).toList()
    }

    private fun convertUserToUserSearchDTO(userDto: UserDTO, requesterEmail: String): UserSearchDTO {
        val targetUser = userRelationshipService.findByEmail(userDto.email)
            ?: throw EmailNotFoundException("Target user email not found")

        val requesterFollowsUser = followsUser(requesterEmail, targetUser.id)
        val requesterIsFollowed = isUserFollowingRequester(requesterEmail, targetUser)
        val mutualFriendsList = getMutualFriends(targetUser, requesterEmail)

        val topThreeMutualFriends = mutualFriendsList.map { modelMapper.map(it) }.take(3)

        val topThreeMutualFriendsSearchDTOList = topThreeMutualFriends.map { dto ->
            UserSearchDTO.Builder()
                .builder(dto.name, dto.email)
                .bio(dto.bio)
                .picture(dto.picture)
                .joinedDate(dto.createdAt)
                .isOnline(false)
                .followerSize(targetUser.followers.size)
                .followingSize(targetUser.followings.size)
                .isFollowedByRequester(requesterFollowsUser)
                .isFollowingRequester(requesterIsFollowed)
                .mutualFriendsSize(0)
                .topThreeMutualFriends(emptyList())
                .build()
        }

        return UserSearchDTO.Builder()
            .builder(targetUser.name, targetUser.email)
            .bio(targetUser.bio)
            .picture(targetUser.picture)
            .joinedDate(formattedDate(targetUser.createdAt))
            .isOnline(false)
            .followerSize(targetUser.followers.size)
            .followingSize(targetUser.followings.size)
            .isFollowedByRequester(requesterFollowsUser)
            .isFollowingRequester(requesterIsFollowed)
            .mutualFriendsSize(mutualFriendsList.size)
            .topThreeMutualFriends(topThreeMutualFriendsSearchDTOList)
            .build()
    }

    private fun formattedDate(instant: Instant): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyy")
            .withZone(ZoneId.systemDefault())
        return dateTimeFormatter.format(instant)
    }
}