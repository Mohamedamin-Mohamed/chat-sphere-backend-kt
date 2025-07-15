package com.chatsphere.kotlin.converter

import com.chatsphere.kotlin.dto.UserSearchDTO
import com.chatsphere.kotlin.dto.UserSearchResponse
import com.chatsphere.kotlin.model.User
import formatedDate
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class UserSearchResponseToDTOConverter : Function<UserSearchResponse, UserSearchDTO> {
    override fun apply(userSearchResponse: UserSearchResponse): UserSearchDTO{
        val user: User? = userSearchResponse.user
        val isFollowedByRequester: Boolean = userSearchResponse.requesterFollowsUser
        val isFollowingRequester: Boolean = userSearchResponse.requesterIsFollowed
        val mutualFriends: List<User> = userSearchResponse.mutualFriendList

        val mutualFriendsSize = mutualFriends.size

        val topThreeMutualFriends = userSearchResponse.topThreeMutualFriendsSearchDTOList

        return UserSearchDTO
            .Builder(user?.email, user?.name)
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
}