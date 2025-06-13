package com.chatsphere.kotlin.dto

class UserSearchDTO private constructor(
    val email: String?,
    val name: String?,
    val bio: String?,
    val picture: String?,
    val joinedDate: String?,
    val isOnline: Boolean,
    val followerSize: Int?,
    val followingSize: Int?,
    val isFollowedByRequester: Boolean,
    val isFollowingRequester: Boolean,
    val mutualFriendsSize: Int?,
    topThreeMutualFriends: List<UserSearchDTO>
) {
   class Builder{
       private var email: String? = null
       private var name: String? = null
       private var bio: String? = null
       private var picture: String? = null
       private var joinedDate: String? = null
       private var isOnline: Boolean = false
       private var followerSize: Int? = null
       private var followingSize: Int? = null
       private var isFollowedByRequester: Boolean = false
       private var isFollowingRequester: Boolean = false
       private var mutualFriendsSize: Int? =  null
       private var topThreeMutualFriends: List<UserSearchDTO> = listOf()

       fun builder(name: String?, email: String?) = apply {
           this.name = name
           this.email = email
       }
       fun bio(bio: String?) = apply { this.bio = bio }
       fun picture(picture: String?) = apply { this.picture = picture }
       fun joinedDate(joinedDate: String?) = apply { this.joinedDate = joinedDate }
       fun isOnline(isOnline: Boolean) = apply { this.isOnline = isOnline }
       fun followerSize(followerSize: Int?) = apply { this.followerSize = followerSize }
       fun followingSize(followingSize: Int?) = apply { this.followingSize = followingSize }
       fun isFollowedByRequester(isFollowedByRequester: Boolean) = apply { this.isFollowedByRequester = isFollowedByRequester }
       fun isFollowingRequester(isFollowingRequester: Boolean) = apply { this.isFollowingRequester = isFollowingRequester }
       fun mutualFriendsSize(mutualFriendsSize: Int) = apply { this.mutualFriendsSize = mutualFriendsSize }
       fun topThreeMutualFriends(topThreeMutualFriends: List<UserSearchDTO>) = apply { this.topThreeMutualFriends = topThreeMutualFriends }
       fun build() = UserSearchDTO(email, name, bio, picture, joinedDate, isOnline, followerSize, followingSize, isFollowedByRequester, isFollowingRequester, mutualFriendsSize, topThreeMutualFriends)
   }
}