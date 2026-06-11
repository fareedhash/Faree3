package com.example.ui

import com.example.ui.theme.*
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ChatMessage
import com.example.data.User
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val activePartner by viewModel.activeChatPartner.collectAsState()
    val chatHistory by viewModel.currentChatMessages.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showAiPromptDialog by remember { mutableStateOf(false) }

    val isRecording by viewModel.isRecordingVoice.collectAsState()
    val recordingDuration by viewModel.recordingDurationSec.collectAsState()

    // Timer simulation for voice recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000)
                viewModel.incrementVoiceDuration()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Transparent)) {
        if (showAiPromptDialog) {
            val aiPrompt by viewModel.aiGenPrompt.collectAsState()
            val aiStatus by viewModel.aiGenStatus.collectAsState()
            val aiUrl by viewModel.aiGeneratedPicUrl.collectAsState()

            var promptInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    showAiPromptDialog = false
                    viewModel.resetAIGenerator()
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Image Prompt Generator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Type an art prompt to generate a brand-new custom AI picture to send in this chat.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("E.g., cosmic wizard knight, cyberpunk skyline", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        if (aiStatus == "generating") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Generating artwork...", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Connecting to Imagen Service...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                }
                            }
                        } else if (aiStatus == "success" && aiUrl != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Preview: \"$aiPrompt\"",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            AsyncImage(
                                model = aiUrl,
                                contentDescription = "AI Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (aiStatus == "success" && aiUrl != null) {
                            Button(
                                onClick = {
                                    viewModel.sendMessage("AI Gen: \"$aiPrompt\"", "photo", aiUrl)
                                    showAiPromptDialog = false
                                    viewModel.resetAIGenerator()
                                },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Send in Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (promptInput.isNotBlank()) {
                                        viewModel.generateAIPhoto(promptInput)
                                    }
                                },
                                enabled = promptInput.isNotBlank() && aiStatus != "generating",
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Generate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAiPromptDialog = false
                            viewModel.resetAIGenerator()
                        },
                        enabled = aiStatus != "generating"
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        if (activePartner == null) {
            // Select Partner screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Forum,
                    contentDescription = "Chats",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "SELECT RECIPIENT",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,
                    fontSize = 15.sp
                )
                Text(
                    text = "Who would you like to message privately?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    items(users.filter { it.id != currentUser?.id }) { partner ->
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectChatPartner(partner) }
                                .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
                            headlineContent = { Text(partner.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(partner.email, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp) },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(partner.avatarEmoji, fontSize = 20.sp)
                                }
                            },
                            trailingContent = {
                                Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = MaterialTheme.colorScheme.primary)
                            }
                        )
                    }
                }
            }
        } else {
            // Active Chat screen
            Column(modifier = Modifier.fillMaxSize()) {
                // Partner Chat Header Block
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(18.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(activePartner?.avatarEmoji ?: "👤", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(activePartner?.name ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Secure Private Chat", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.selectChatPartner(null) }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    actions = {
                        // Clear Chat locally
                        Button(
                            onClick = { viewModel.clearLocalChatHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Filled.PhonelinkErase, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", color = MaterialTheme.colorScheme.onError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                // NO CALLING EXTREMELY CLEAR BANNERS (Restriction Requirement 1)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "⚠️ Telephonic Audio/Video calling has been completely deactivated on this secure node.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Chat Messages Feed list
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    if (chatHistory.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Your secure log is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Text("Send a text, emoji, voice note, or AI picture!", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            items(chatHistory) { msg ->
                                val isSelf = msg.senderId == currentUser?.id
                                ChatMsgBubble(message = msg, isSelf = isSelf)
                            }
                        }
                    }
                }

                // Bottom Attachment Menu Drawer
                AnimatedVisibility(
                    visible = showAttachmentMenu,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttachmentOption(
                            icon = Icons.Filled.AutoAwesome,
                            label = "AI Image",
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            showAiPromptDialog = true
                            showAttachmentMenu = false
                        }

                        AttachmentOption(
                            icon = Icons.Filled.EmojiEmotions,
                            label = "Emoji",
                            color = MaterialTheme.colorScheme.secondary
                        ) {
                            viewModel.sendMessage("🔥🚀👾⚡🎮", "emoji")
                            showAttachmentMenu = false
                        }

                        AttachmentOption(
                            icon = Icons.Filled.AddPhotoAlternate,
                            label = "Photo",
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            viewModel.sendMessage("Attached simulated polaroid photo.", "photo", "https://image.pollinations.ai/p/beautiful_landscape_photography_matrix_digital_glow")
                            showAttachmentMenu = false
                        }

                        AttachmentOption(
                            icon = Icons.Filled.VideocamOff, // Clear videocam is disabled
                            label = "Video",
                            color = MaterialTheme.colorScheme.error
                        ) {
                            viewModel.sendMessage("Attached short video simulation clip.", "video", "https://image.pollinations.ai/p/futuristic_gaming_room_setup")
                            showAttachmentMenu = false
                        }

                        AttachmentOption(
                            icon = Icons.Filled.FilePresent,
                            label = "File",
                            color = MaterialTheme.colorScheme.secondary
                        ) {
                            viewModel.sendMessage("Contract_Audit_Review_Kazam.pdf", "file", "/documents/Kazam_Review.pdf")
                            showAttachmentMenu = false
                        }
                    }
                }

                // Input Action Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(10.dp)
                    ) {
                        IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                            Icon(
                                imageVector = if (showAttachmentMenu) Icons.Filled.Close else Icons.Filled.AddCircleOutline,
                                contentDescription = "Attachments",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Voice Recorder Widget
                        if (isRecording) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(24.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.error)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Recording Voice: $recordingDuration sec",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { viewModel.cancelVoiceRecording() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { viewModel.stopAndSendVoiceRecording() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Check, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                placeholder = { Text("Enter secret message...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 12.sp) },
                                modifier = Modifier.weight(1.0f),
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // Action triggers: Text Send vs mic hold
                            if (messageText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        viewModel.sendMessage(messageText, "text")
                                        messageText = ""
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            } else {
                                // Microphone recorder trigger button
                                IconButton(
                                    onClick = { viewModel.startSimulationVoiceRecording() },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Filled.Mic, contentDescription = "Record Voice Note", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMsgBubble(message: ChatMessage, isSelf: Boolean) {
    val bubbleColor = if (isSelf) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f)
    val alignment = if (isSelf) Alignment.CenterEnd else Alignment.CenterStart

    var isSimulatedPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isSimulatedPlaying) {
        if (isSimulatedPlaying) {
            delay(message.voiceDurationSec * 1000L)
            isSimulatedPlaying = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = bubbleColor),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isSelf) 16.dp else 4.dp,
                    bottomEnd = if (isSelf) 4.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 280.dp),
                border = BorderStroke(1.2.dp, if (isSelf) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Headline
                    Text(
                        text = if (isSelf) "You" else message.senderName,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelf) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    when (message.type) {
                        "text" -> {
                            Text(text = message.content, color = if (isSelf) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                        "emoji" -> {
                            Text(text = message.content, fontSize = 28.sp)
                        }
                        "voice" -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isSimulatedPlaying = !isSimulatedPlaying }
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSimulatedPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = "Play/Stop Voice",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (isSimulatedPlaying) "Playing Node..." else "Speech Message",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Animated Waveform mock
                                    Text(
                                        text = if (isSimulatedPlaying) "||ı||ı|ı||ı|ı||" else "||||||||||||||",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "0:${message.voiceDurationSec.toString().padStart(2, '0')}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        "photo" -> {
                            AsyncImage(
                                model = message.fileUriHint,
                                contentDescription = "Instant polaroid",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = message.content, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                        "video" -> {
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = message.fileUriHint,
                                    contentDescription = "Sim video frame",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play clip", tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = message.content, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                        "file" -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                Icon(Icons.Filled.FilePresent, contentDescription = "File", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(message.content, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("PDF File System • Secured Node", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
            Text(
                text = "Secured",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
