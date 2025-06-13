package com.chatsphere.kotlin.dto

import com.chatsphere.kotlin.model.User

class UserSearchResponse private constructor(
    val user: User?,
    val requesterFollowsUser: Boolean,
    val requesterIsFollowed: Boolean,
    val mutualFriendList: List<User>,
    val topThreeMutualFriendsSearchDTOList: List<UserSearchDTO>
) {
    class Builder{
        private var user: User? = null
        private var requesterFollowsUser: Boolean = false
        private var requesterIsFollowed: Boolean = false
        private var mutualFriendList: List<User> = listOf()
        private var topThreeMutualFriendsSearchDTOList: List<UserSearchDTO> = listOf()

        fun user(user: User) = apply { this.user = user }
        fun requesterFollowsUser(requesterFollowsUser: Boolean) = apply { this.requesterFollowsUser = requesterFollowsUser }
        fun requesterIsFollowed(requesterIsFollowed: Boolean) = apply { this.requesterIsFollowed = requesterIsFollowed }
        fun mutualFriendsList(mutualFriendList: List<User>) = apply { this.mutualFriendList = mutualFriendList }
        fun topThreeMutualFriendsSearchDTOList(topThreeMutualFriendsSearchDTOList: List<UserSearchDTO>) = apply { this.topThreeMutualFriendsSearchDTOList = topThreeMutualFriendsSearchDTOList }
        fun build(): UserSearchResponse = UserSearchResponse(user, requesterFollowsUser, requesterIsFollowed, mutualFriendList, topThreeMutualFriendsSearchDTOList)

    }
}