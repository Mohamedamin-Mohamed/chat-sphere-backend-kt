package com.chatsphere.kotlin.model

import jakarta.persistence.*
import org.springframework.lang.NonNull
import java.util.UUID

@Entity
@Table(name = "Message")
class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var messageID: UUID? = null

    @NonNull
    var email: String? = null

    @NonNull
    var sender: String? = null

    @NonNull
    @Column(columnDefinition = "TEXT")
    var message: String? = null

    @NonNull
    var timestamp: String? = null

}