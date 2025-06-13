package com.chatsphere.kotlin.model


import com.chatsphere.kotlin.util.Role
import jakarta.persistence.*
import org.springframework.lang.NonNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

@Entity
@Table(name = "Users")
class User : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Column(name = "created_at", updatable = false)
    lateinit var createdAt: Instant

    @PrePersist
    fun onCreate(): Unit {
        createdAt = Instant.now()
    }

    @Column(name = "full_name")
    var name: String? = null

    @Column(unique = true)
    @NonNull
    var email: String? = null

    @NonNull
    var passwd: String = ""
    var oauthProvider: String? = null

    @Column(unique = true)
    var oauthId: String? = null

    var emailVerified: Boolean = false

    @Column(name = "picture_url")
    var picture: String? = null

    var authorizationCode: String? = null
    var bio: String? = null
    var phoneNumber: String? = null
    var identityToken: String? = null
    var accessToken: String? = null

    @OneToMany(mappedBy = "following")
    var followers: MutableList<Follow> = mutableListOf()

    @OneToMany(mappedBy = "follower")
    var followings: MutableList<Follow> = mutableListOf()

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return passwd
    }

    override fun getUsername(): String? {
        return email
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}