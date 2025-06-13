package com.chatsphere.kotlin.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "Follows", uniqueConstraints = [UniqueConstraint(columnNames = ["follower_id", "following_id"])])
class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L

    @ManyToOne
    @JoinColumn(name = "follower_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    var follower: User? = null

    @ManyToOne
    @JoinColumn(name = "following_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    var following: User? = null


}