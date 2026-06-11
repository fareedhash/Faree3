package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.UUID

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db)

    // --- Active User Context ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // --- Active Chat Room Partner ---
    private val _activeChatPartner = MutableStateFlow<User?>(null)
    val activeChatPartner: StateFlow<User?> = _activeChatPartner.asStateFlow()

    // --- All Registries (from Room) ---
    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedPosts: StateFlow<List<Post>> = repository.getVisiblePosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllPosts: StateFlow<List<Post>> = repository.getAdminAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGameLobbies: StateFlow<List<GameLobby>> = repository.getAllLobbies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLog>> = repository.getLatestLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAiImages: StateFlow<List<AiImage>> = repository.getAllAiImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllMessages: StateFlow<List<ChatMessage>> = repository.getAdminAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Direct dynamic query for chat screens (between currentUser and activeChatPartner)
    private val _currentChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentChatMessages: StateFlow<List<ChatMessage>> = _currentChatMessages.asStateFlow()

    // --- AI Image Generator States ---
    private val _aiGenPrompt = MutableStateFlow("")
    val aiGenPrompt: StateFlow<String> = _aiGenPrompt.asStateFlow()

    private val _aiGenStatus = MutableStateFlow<String>("idle") // "idle", "generating", "success", "error"
    val aiGenStatus: StateFlow<String> = _aiGenStatus.asStateFlow()

    private val _aiGeneratedPicUrl = MutableStateFlow<String?>(null)
    val aiGeneratedPicUrl: StateFlow<String?> = _aiGeneratedPicUrl.asStateFlow()

    // --- Voice Recording State ---
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordingDurationSec = MutableStateFlow(0)
    val recordingDurationSec: StateFlow<Int> = _recordingDurationSec.asStateFlow()

    // --- Active Game Lobby --
    private val _activeLobby = MutableStateFlow<GameLobby?>(null)
    val activeLobby: StateFlow<GameLobby?> = _activeLobby.asStateFlow()

    private val _selectedPostIdForComments = MutableStateFlow<Long?>(null)
    val selectedPostIdForComments: StateFlow<Long?> = _selectedPostIdForComments.asStateFlow()

    val currentPostComments: StateFlow<List<Comment>> = _selectedPostIdForComments
        .flatMapLatest { id ->
            if (id != null) repository.getCommentsForPost(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Guarantee sample data and system users are seeded on cold start
            repository.checkAndPrepopulateUsers()

            // Default to first user ("Fareed")
            val users = repository.getAllUsers().firstOrNull() ?: emptyList()
            val fareed = users.firstOrNull { it.id == 1 } ?: users.firstOrNull()
            _currentUser.value = fareed

            // Set up chat collector whenever user swaps
            combine(currentUser, activeChatPartner) { user, partner ->
                Pair(user, partner)
            }.collect { (user, partner) ->
                if (user != null && partner != null) {
                    collectChatMessages(user.id, partner.id)
                } else {
                    _currentChatMessages.value = emptyList()
                }
            }
        }
    }

    // Swaps profile context so user can test various functionalities (Admin vs Normal)
    fun switchCurrentUser(user: User) {
        viewModelScope.launch {
            _currentUser.value = user
            // Log access behavior
            repository.logAction(
                userId = user.id,
                userEmail = user.email,
                userName = user.name,
                action = "PROFILE_SWAP",
                details = "Swapped current session context securely to user profile."
            )
        }
    }

    fun selectChatPartner(partner: User?) {
        _activeChatPartner.value = partner
    }

    private fun collectChatMessages(selfId: Int, partnerId: Int) {
        viewModelScope.launch {
            repository.getVisibleChatHistory(selfId, partnerId).collect {
                _currentChatMessages.value = it
            }
        }
    }

    // --- Chat Room Communications (Text, Emoji, Voice, File, AI Picture) ---
    fun sendMessage(content: String, type: String = "text", fileUriHint: String? = null, voiceDuration: Int = 0) {
        val self = currentUser.value ?: return
        val partner = activeChatPartner.value ?: return

        viewModelScope.launch {
            val message = ChatMessage(
                senderId = self.id,
                senderName = self.name,
                receiverId = partner.id,
                receiverName = partner.name,
                content = content,
                type = type,
                fileUriHint = fileUriHint,
                voiceDurationSec = voiceDuration
            )
            repository.insertMessage(message)
        }
    }

    // Clear local conversation history for seeing soft-delete
    fun clearLocalChatHistory() {
        val self = currentUser.value ?: return
        val partner = activeChatPartner.value ?: return

        viewModelScope.launch {
            repository.clearChatHistory(self.id, partner.id, self)
            // Refresh local flow
            collectChatMessages(self.id, partner.id)
        }
    }

    // --- Voice Recording Simulation ---
    fun startSimulationVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingDurationSec.value = 0
    }

    fun incrementVoiceDuration() {
        if (_isRecordingVoice.value) {
            _recordingDurationSec.value += 1
        }
    }

    fun stopAndSendVoiceRecording() {
        if (!_isRecordingVoice.value) return
        val duration = _recordingDurationSec.value
        _isRecordingVoice.value = false
        _recordingDurationSec.value = 0
        if (duration > 0) {
            sendMessage(
                content = "Voice note simulation recording completed ($duration sec).",
                type = "voice",
                voiceDuration = duration
            )
        }
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _recordingDurationSec.value = 0
    }

    // --- Social Media Feed Actions ---
    fun createPost(content: String, mediaType: String? = null, mediaUri: String? = null) {
        val self = currentUser.value ?: return
        viewModelScope.launch {
            val post = Post(
                authorId = self.id,
                authorName = self.name,
                authorAvatar = self.avatarEmoji,
                content = content,
                mediaType = mediaType,
                mediaUri = mediaUri
            )
            repository.insertPost(post, self)
        }
    }

    fun likePost(postId: Long) {
        val self = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleLikePost(postId, self)
        }
    }

    fun incrementViews(postId: Long) {
        viewModelScope.launch {
            repository.incrementPostViews(postId)
        }
    }

    fun deletePostSoft(postId: Long) {
        val self = currentUser.value ?: return
        viewModelScope.launch {
            repository.softDeletePost(postId, self)
        }
    }

    // --- Post Detail Comments Window ---
    fun viewPostComments(postId: Long?) {
        _selectedPostIdForComments.value = postId
        if (postId != null) {
            incrementViews(postId)
        }
    }

    fun submitComment(content: String) {
        val self = currentUser.value ?: return
        val postId = selectedPostIdForComments.value ?: return
        viewModelScope.launch {
            val comment = Comment(
                postId = postId,
                authorId = self.id,
                authorName = self.name,
                authorAvatar = self.avatarEmoji,
                content = content
            )
            repository.insertComment(comment, self)
        }
    }

    // --- Integrated AI Image Generator ---
    fun generateAIPhoto(prompt: String) {
        if (prompt.isBlank()) return
        val self = currentUser.value ?: return

        _aiGenPrompt.value = prompt
        _aiGenStatus.value = "generating"
        _aiGeneratedPicUrl.value = null

        viewModelScope.launch {
            // Simulate AI computation wait
            kotlinx.coroutines.delay(2000)

            // Dynamic Pollinations AI call which actually generates matching graphics
            val sanitizedPrompt = try {
                URLEncoder.encode(prompt.trim(), "UTF-8")
                    .replace("+", "_")
            } catch (e: UnsupportedEncodingException) {
                prompt.replace(" ", "_").replace("[^a-zA-Z0-9_]".toRegex(), "")
            }

            // Real pollen URL configuration
            val resultUrl = "https://image.pollinations.ai/p/$sanitizedPrompt"

            _aiGeneratedPicUrl.value = resultUrl
            _aiGenStatus.value = "success"

            // Save AI image and prompt to secure local Database ledger
            repository.insertAiImage(
                AiImage(
                    userId = self.id,
                    userName = self.name,
                    userEmail = self.email,
                    prompt = prompt,
                    imageUrl = resultUrl
                )
            )

            // Log AI transaction silently in background
            repository.logAction(
                userId = self.id,
                userEmail = self.email,
                userName = self.name,
                action = "AI_IMAGE_GENERATED",
                details = "Generated picture using Pollinations AI. Input prompt: \"$prompt\""
            )
        }
    }

    fun resetAIGenerator() {
        _aiGenPrompt.value = ""
        _aiGenStatus.value = "idle"
        _aiGeneratedPicUrl.value = null
    }

    // --- Multiplayer Matchmaking Lobbies & Games ---
    fun createGameLobby(lobbyName: String, format: String) {
        val self = currentUser.value ?: return
        viewModelScope.launch {
            val lobbyId = UUID.randomUUID().toString().take(6).uppercase()
            val lobby = GameLobby(
                lobbyId = lobbyId,
                name = lobbyName.ifBlank { "Lobby $lobbyId" },
                hostId = self.id,
                hostName = self.name,
                format = format,
                status = "waiting",
                // Set initial members slots based on formats
                team1Members = "${self.name} (Host)",
                team2Members = "AI Drone"
            )
            repository.insertLobby(lobby, self)
            _activeLobby.value = lobby
        }
    }

    fun selectLobby(lobby: GameLobby?) {
        _activeLobby.value = lobby
    }

    // HOST ONLY capability to swap sloted placements / configure slots manually
    fun swapLobbyPlacements() {
        val self = currentUser.value ?: return
        val active = activeLobby.value ?: return
        if (self.id != active.hostId) return // Ensure strictly host-restricted

        // Reorganize Slots: swap team lists
        val temp = active.team1Members
        val updatedLobby = active.copy(
            team1Members = active.team2Members,
            team2Members = temp
        )
        viewModelScope.launch {
            repository.updateLobby(updatedLobby, self, "Host manually swapped team 1 and team 2 slot mappings.")
            _activeLobby.value = updatedLobby
        }
    }

    // HOST ONLY: Configure dynamic player slots (e.g., adding an invite virtual member to slots)
    fun addVirtualPlayerToSlots(teamIndex: Int, virtName: String) {
        val self = currentUser.value ?: return
        val active = activeLobby.value ?: return
        if (self.id != active.hostId) return

        val normalizedName = virtName.ifBlank { "Virtual Challenger" }
        val updatedLobby = if (teamIndex == 1) {
            active.copy(team1Members = if (active.team1Members.isBlank()) normalizedName else "${active.team1Members}, $normalizedName")
        } else {
            active.copy(team2Members = if (active.team2Members.isBlank()) normalizedName else "${active.team2Members}, $normalizedName")
        }

        viewModelScope.launch {
            repository.updateLobby(updatedLobby, self, "Host added a slot challenger '$normalizedName' manually.")
            _activeLobby.value = updatedLobby
        }
    }

    // Start running the instant HTML5 game
    fun launchMatch() {
        val self = currentUser.value ?: return
        val active = activeLobby.value ?: return
        val updatedLobby = active.copy(status = "playing")
        viewModelScope.launch {
            repository.updateLobby(updatedLobby, self, "Match running instant HTML5 WebView frame container.")
            _activeLobby.value = updatedLobby
        }
    }

    // Match Complete: highlight winner, award trophies into room ledger, write to server log
    fun handleMatchFinished(winner: String) {
        val active = activeLobby.value ?: return
        if (active.status != "playing") return

        val self = currentUser.value ?: return
        val isSelfWinner = (winner == self.name || winner.contains("Host"))

        viewModelScope.launch {
            val updatedLobby = active.copy(status = "finished", winnerName = winner)
            repository.updateLobby(updatedLobby, self, "Match complete! Winner: $winner. Permanent Trophy updated in ledger.")

            // Award trophy if current user or mapped participant won
            if (isSelfWinner) {
                val updatedSelf = self.copy(trophies = self.trophies + 1)
                repository.updateUser(updatedSelf)
                _currentUser.value = updatedSelf
            } else {
                // Award to other player slot representation
                val users = repository.getAllUsers().firstOrNull() ?: emptyList()
                val otherUser = users.firstOrNull { winner.contains(it.name) }
                if (otherUser != null) {
                    repository.updateUser(otherUser.copy(trophies = otherUser.trophies + 1))
                }
            }

            _activeLobby.value = updatedLobby
        }
    }

    fun leaveActiveLobby() {
        _activeLobby.value = null
    }

    // Delete lobby room
    fun dismissLobby(lobbyId: String) {
        val self = currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteLobby(lobbyId, self)
            if (_activeLobby.value?.lobbyId == lobbyId) {
                _activeLobby.value = null
            }
        }
    }
}
