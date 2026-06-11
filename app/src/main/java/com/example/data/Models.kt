package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val avatarEmoji: String,
    val trophies: Int = 0,
    val isAdmin: Boolean = false
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: Int,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val mediaType: String? = null, // "text", "photo", "ai_photo", "video"
    val mediaUri: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isHidden: Boolean = false // user soft-delete flag
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorId: Int,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: Int,
    val senderName: String,
    val receiverId: Int,
    val receiverName: String,
    val content: String,
    val type: String, // "text", "emoji", "voice", "photo", "video", "file"
    val fileUriHint: String? = null, // dynamic location of attachments
    val voiceDurationSec: Int = 0, // for recorded voice notes
    val timestamp: Long = System.currentTimeMillis(),
    val isHiddenForSender: Boolean = false, // soft-deletion for Sender
    val isHiddenForReceiver: Boolean = false // soft-deletion for Receiver
)

@Entity(tableName = "game_lobbies")
data class GameLobby(
    @PrimaryKey val lobbyId: String,
    val name: String,
    val hostId: Int,
    val hostName: String,
    val format: String, // "1vs1", "1vs2", "2vs2", "5vs5" etc.
    val status: String, // "waiting", "playing", "finished"
    // Simple serialized comma-separated strings for team slots to avoid reflection issues
    val team1Members: String, // Comma-separated user names/ids in slot format
    val team2Members: String, // Comma-separated user names/ids in slot format
    val winnerName: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: Int,
    val userEmail: String,
    val userName: String,
    val action: String, // "LOGIN", "SEND_MESSAGE", "AI_IMAGE_GENERATED", "CLEAR_CHAT_HISTORY", etc.
    val details: String
)

@Entity(tableName = "ai_images")
data class AiImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val prompt: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
