package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val postDao = db.postDao()
    private val commentDao = db.commentDao()
    private val chatDao = db.chatDao()
    private val gameLobbyDao = db.gameLobbyDao()
    private val auditLogDao = db.auditLogDao()
    private val aiImageDao = db.aiImageDao()

    // --- Users ---
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    // Pre-populate system users if none exist
    suspend fun checkAndPrepopulateUsers() {
        val users = userDao.getAllUsers().firstOrNull() ?: emptyList()
        if (users.isEmpty()) {
            val adminUser = User(
                id = 999,
                name = "Kazam (Master Admin)",
                email = "fareedgullam571@gmail.com",
                avatarEmoji = "👑",
                trophies = 99,
                isAdmin = true
            )
            val normalUser1 = User(
                id = 1,
                name = "Fareed",
                email = "fareed@kazam.com",
                avatarEmoji = "⚡",
                trophies = 12,
                isAdmin = false
            )
            val normalUser2 = User(
                id = 2,
                name = "Mr Kazam",
                email = "mr.kazam@example.com",
                avatarEmoji = "🎮",
                trophies = 34,
                isAdmin = false
            )
            val botUser = User(
                id = 42,
                name = "AI Bot",
                email = "ai-bot@mr-kazam.ai",
                avatarEmoji = "🤖",
                trophies = 5,
                isAdmin = false
            )

            insertUser(adminUser)
            insertUser(normalUser1)
            insertUser(normalUser2)
            insertUser(botUser)

            // Silent system seeding log
            logAction(
                userId = 999,
                userEmail = adminUser.email,
                userName = adminUser.name,
                action = "SYSTEM_SEEDING",
                details = "Initialized database default Master Admin, Fareed, Mr Kazam, and AI Bot."
            )

            // Let's seed some posts
            val post1 = Post(
                authorId = 2,
                authorName = "Mr Kazam",
                authorAvatar = "🎮",
                content = "Welcome to the ultimate hybrid gaming, private social, and AI canvas app MR_KAZAM_FNT_N. Explore chat audio recordings or custom Web lobbies!",
                likesCount = 23,
                commentsCount = 2,
                viewsCount = 57
            )
            val post2 = Post(
                authorId = 42,
                authorName = "AI Bot",
                authorAvatar = "🤖",
                content = "Generated high-contrast abstract AI artwork: futuristic neon knight in a quantum computing universe. Ready for games?",
                mediaType = "ai_photo",
                mediaUri = "https://image.pollinations.ai/p/futuristic_neon_knight_gaming_aesthetic_cyberpunk",
                likesCount = 45,
                commentsCount = 1,
                viewsCount = 112
            )

            val p1Id = postDao.insertPost(post1)
            val p2Id = postDao.insertPost(post2)

            commentDao.insertComment(Comment(postId = p1Id, authorId = 1, authorName = "Fareed", authorAvatar = "⚡", content = "This app is blazingly fast in native Kotlin Compose! Love it."))
            commentDao.insertComment(Comment(postId = p1Id, authorId = 42, authorName = "AI Bot", authorAvatar = "🤖", content = "Indeed! Everything is reactive with Room StateFlows."))
            commentDao.insertComment(Comment(postId = p2Id, authorId = 2, authorName = "Mr Kazam", authorAvatar = "🎮", content = "That AI graphic looks stellar!"))
        }
    }

    // --- Posts ---
    fun getVisiblePosts(): Flow<List<Post>> = postDao.getVisiblePosts()

    fun getAdminAllPosts(): Flow<List<Post>> = postDao.getAdminAllPosts()

    suspend fun insertPost(post: Post, user: User): Long {
        val postId = postDao.insertPost(post)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = if (post.mediaType == "ai_photo") "AI_POST_CREATED" else "POST_CREATED",
            details = "Published new post [ID:$postId]: ${post.content.take(50)}"
        )
        return postId
    }

    suspend fun toggleLikePost(postId: Long, user: User) {
        val post = postDao.getPostById(postId) ?: return
        // Toggle simulated like count increment
        val updatedPost = post.copy(likesCount = post.likesCount + 1)
        postDao.updatePost(updatedPost)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "POST_LIKE",
            details = "Liked post [ID:$postId]"
        )
    }

    suspend fun incrementPostViews(postId: Long) {
        postDao.incrementViewCount(postId)
    }

    suspend fun softDeletePost(postId: Long, user: User) {
        val post = postDao.getPostById(postId) ?: return
        postDao.updatePost(post.copy(isHidden = true))
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "POST_SOFT_DELETE",
            details = "User flagged post [ID:$postId] as hidden."
        )
    }

    // --- Comments ---
    fun getCommentsForPost(postId: Long): Flow<List<Comment>> = commentDao.getCommentsForPost(postId)

    suspend fun insertComment(comment: Comment, user: User) {
        commentDao.insertComment(comment)
        postDao.incrementCommentCount(comment.postId)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "COMMENT_CREATED",
            details = "Commented on post [ID:${comment.postId}]: ${comment.content.take(40)}"
        )
    }

    // --- Private Chats ---
    fun getVisibleChatHistory(userA: Int, userB: Int): Flow<List<ChatMessage>> =
        chatDao.getVisibleChatHistory(userA, userB)

    fun getAdminAllMessages(): Flow<List<ChatMessage>> = chatDao.getAdminAllMessages()

    suspend fun insertMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
        logAction(
            userId = message.senderId,
            userEmail = if (message.senderId == 999) "fareedgullam571@gmail.com" else "user-${message.senderId}@example.com",
            userName = message.senderName,
            action = "SEND_CHAT_MESSAGE",
            details = "Sent message [Type:${message.type}] to UserID:${message.receiverId}."
        )
    }

    suspend fun clearChatHistory(currentUserId: Int, otherUserId: Int, user: User) {
        chatDao.clearHistoryAsSender(currentUserId, otherUserId)
        chatDao.clearHistoryAsReceiver(currentUserId, otherUserId)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "CLEAR_CHAT_HISTORY",
            details = "Cleared messaging history locally with UserID:$otherUserId."
        )
    }

    // --- Lobbies ---
    fun getAllLobbies(): Flow<List<GameLobby>> = gameLobbyDao.getAllLobbies()

    suspend fun getLobbyById(id: String): GameLobby? = gameLobbyDao.getLobbyById(id)

    suspend fun insertLobby(lobby: GameLobby, user: User) {
        gameLobbyDao.insertLobby(lobby)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "LOBBY_CREATED",
            details = "Created game lobby [ID:${lobby.lobbyId}]: Format ${lobby.format}."
        )
    }

    suspend fun updateLobby(lobby: GameLobby, user: User, auditText: String) {
        gameLobbyDao.updateLobby(lobby)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "LOBBY_UPDATED",
            details = "Lobby [ID:${lobby.lobbyId}]: $auditText"
        )
    }

    suspend fun deleteLobby(id: String, user: User) {
        gameLobbyDao.deleteLobby(id)
        logAction(
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            action = "LOBBY_DELETED",
            details = "Removed lobby [ID:$id]."
        )
    }

    // --- Audit Logs ---
    fun getLatestLogs(): Flow<List<AuditLog>> = auditLogDao.getLatestLogs()

    // --- AI Generated Images ---
    fun getAllAiImages(): Flow<List<AiImage>> = aiImageDao.getAllAiImages()

    suspend fun insertAiImage(aiImage: AiImage) {
        aiImageDao.insertAiImage(aiImage)
    }

    suspend fun logAction(userId: Int, userEmail: String, userName: String, action: String, details: String) {
        auditLogDao.insertLog(
            AuditLog(
                userId = userId,
                userEmail = userEmail,
                userName = userName,
                action = action,
                details = details
            )
        )
    }
}
