package com.chatsphere.kotlin.mapper

import com.chatsphere.kotlin.dto.*
import com.chatsphere.kotlin.model.Follow
import com.chatsphere.kotlin.model.Message
import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.service.JwtAuthService
import com.chatsphere.kotlin.service.PasswordService
import com.chatsphere.kotlin.util.Role
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class ModelMapper(
    val passwordService: PasswordService,
    val jwtAuthService: JwtAuthService
) {
    fun map(signUpRequest: SignUpRequestDTO): User {
        val hashedPassword: String = passwordService.hashPassword(signUpRequest.password)

        val user = User()
        user.email = signUpRequest.email
        user.passwd = hashedPassword
        user.name = signUpRequest.name
        user.role = Role.USER

        return user
    }

    fun map(oAuthSignUpRequest: OAuthSignUpRequestDTO): User {
        val user = User()
        user.email = oAuthSignUpRequest.email
        user.name = oAuthSignUpRequest.name
        user.oauthProvider = oAuthSignUpRequest.oauthProvider
        user.oauthId = oAuthSignUpRequest.oauthId
        user.emailVerified = oAuthSignUpRequest.emailVerified
        user.picture = oAuthSignUpRequest.picture
        user.authorizationCode = oAuthSignUpRequest.authorizationCode
        user.accessToken = oAuthSignUpRequest.accessToken
        user.identityToken = oAuthSignUpRequest.identityToken
        user.role = Role.USER

        return user
    }

    fun map(user: User): UserDTO {
        val token: String = getToken(user)

        return UserDTO
            .Builder()
            .email(user.email ?: "")
            .createdAt(formatedDate(user.createdAt))
            .name(user.name ?: "")
            .oauthProvider(user.oauthProvider ?: "")
            .picture(user.picture ?: "")
            .bio(user.bio ?: "")
            .phoneNumber(user.phoneNumber ?: "")
            .token(token)
            .build()
    }

    fun formatedDate(instant: Instant?): String {
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM yyy").withZone(ZoneId.systemDefault())
        return dateTimeFormatter.format(instant)
    }

    fun map(messageRequest: MessageRequestDTO): Message {
        val message = Message()
        message.email = message.email
        message.sender = messageRequest.sender
        message.message = message.message
        message.timestamp = message.timestamp

        return message
    }

    fun map(message: Message): MessageResponseDTO {
        return MessageResponseDTO
            .Builder()
            .messageId(message.messageID.toString())
            .email(message.email)
            .sender(message.sender)
            .message(message.message)
            .timestamp(message.timestamp)
            .build()
    }

    fun map(follower: User, following: User): Follow {
        val follow = Follow()
        follow.follower = follower
        follow.following = following
        return follow
    }

    fun map(userSearchResponse: UserSearchResponse): UserSearchDTO {
        val user: User? = userSearchResponse.user
        val isFollowedByRequester: Boolean = userSearchResponse.requesterFollowsUser
        val isFollowingRequester: Boolean = userSearchResponse.requesterIsFollowed
        val mutualFriends: List<User> = userSearchResponse.mutualFriendList

        val mutualFriendsSize = mutualFriends.size

        val topThreeMutualFriends = userSearchResponse.topThreeMutualFriendsSearchDTOList

        return UserSearchDTO
            .Builder()
            .builder(user?.name, user?.email)
            .bio(user?.bio)
            .picture(user?.picture)
            .joinedDate(formatedDate(user?.createdAt))
            .isOnline(false)
            .followerSize(user?.followers?.size)
            .followingSize(user?.followings?.size)
            .isFollowedByRequester(isFollowedByRequester)
            .isFollowingRequester(isFollowingRequester)
            .mutualFriendsSize(mutualFriendsSize)
            .topThreeMutualFriends(topThreeMutualFriends)
            .build()
    }

    fun map(followList: List<Follow>, followingList: List<Follow>): UserStatsDTO {
        val userStatsDTO = UserStatsDTO()
        userStatsDTO.followers = followingList.size
        userStatsDTO.followings = followingList.size
        return userStatsDTO
    }

    private fun getToken(user: User): String {
        return jwtAuthService.createToken(user)
    }
}