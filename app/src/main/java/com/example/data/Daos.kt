package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface PostDao {
    // Normal Feed: Show posts that are not hidden
    @Query("SELECT * FROM posts WHERE isHidden = 0 ORDER BY timestamp DESC")
    fun getVisiblePosts(): Flow<List<Post>>

    // Admin Panel: View all posts, including hidden ones
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAdminAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Long): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Update
    suspend fun updatePost(post: Post)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: Long)

    @Query("UPDATE posts SET viewsCount = viewsCount + 1 WHERE id = :postId")
    suspend fun incrementViewCount(postId: Long)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long
}

@Dao
interface ChatDao {
    // Normal Chat Feed: select messages between userA and userB that haven't been cleared by the viewing user
    @Query("""
        SELECT * FROM chat_messages 
        WHERE (senderId = :userA AND receiverId = :userB AND isHiddenForSender = 0)
           OR (senderId = :userB AND receiverId = :userA AND isHiddenForReceiver = 0)
        ORDER BY timestamp ASC
    """)
    fun getVisibleChatHistory(userA: Int, userB: Int): Flow<List<ChatMessage>>

    // Admin Panel: select ALL messages between users
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAdminAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    // Clear History: performs a "soft-delete" on the chat history for a specific user
    @Query("UPDATE chat_messages SET isHiddenForSender = 1 WHERE senderId = :userId AND receiverId = :otherId")
    suspend fun clearHistoryAsSender(userId: Int, otherId: Int)

    @Query("UPDATE chat_messages SET isHiddenForReceiver = 1 WHERE receiverId = :userId AND senderId = :otherId")
    suspend fun clearHistoryAsReceiver(userId: Int, otherId: Int)
}

@Dao
interface GameLobbyDao {
    @Query("SELECT * FROM game_lobbies ORDER BY timestamp DESC")
    fun getAllLobbies(): Flow<List<GameLobby>>

    @Query("SELECT * FROM game_lobbies WHERE lobbyId = :id")
    suspend fun getLobbyById(id: String): GameLobby?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLobby(lobby: GameLobby)

    @Update
    suspend fun updateLobby(lobby: GameLobby)

    @Query("DELETE FROM game_lobbies WHERE lobbyId = :id")
    suspend fun deleteLobby(id: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getLatestLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog): Long
}

@Dao
interface AiImageDao {
    @Query("SELECT * FROM ai_images ORDER BY timestamp DESC")
    fun getAllAiImages(): Flow<List<AiImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiImage(aiImage: AiImage): Long
}
