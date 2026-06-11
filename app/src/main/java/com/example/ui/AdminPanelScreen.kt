package com.example.ui

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
import com.example.data.AuditLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val logs by viewModel.allAuditLogs.collectAsState()
    val allPosts by viewModel.adminAllPosts.collectAsState()
    val allChats by viewModel.adminAllMessages.collectAsState()
    val aiImages by viewModel.allAiImages.collectAsState()

    var activeSubTab by remember { mutableStateOf("AUDIT_LOGS") } // "AUDIT_LOGS", "ARCHIVED_POSTS", "ARCHIVED_CHATS", "AI_IMAGES"

    Box(modifier = modifier.fillMaxSize().background(Color.Transparent)) {
        if (currentUser?.isAdmin != true) {
            // UNAUTHORIZED LOCK CARD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Access Denied",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ADMIN ACCESS BLOCKED",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            letterSpacing = 1.5.sp,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This terminal is strictly restricted to secure administrative nodes. Non-admin users are barred from viewing background metadata, audit logs, or hidden posts.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "To audit this dashboard, please use the top session bar and choose 'Kazam (Master Admin)'.",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // AUTHORIZED ADMIN PANEL
            Column(modifier = Modifier.fillMaxSize()) {
                // Headline bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MASTER CENTRAL INTELLIGENCE",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Secure auditing • No-Deletion database mirrors",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("MASTER MODE ACTIVE", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Sub tabs selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AdminSubTabButton(
                        label = "Logs Audit",
                        icon = Icons.Filled.Assignment,
                        isActive = activeSubTab == "AUDIT_LOGS",
                        modifier = Modifier.weight(1.0f)
                    ) { activeSubTab = "AUDIT_LOGS" }

                    AdminSubTabButton(
                        label = "Archived Posts",
                        icon = Icons.Filled.Book,
                        isActive = activeSubTab == "ARCHIVED_POSTS",
                        modifier = Modifier.weight(1.0f)
                    ) { activeSubTab = "ARCHIVED_POSTS" }

                    AdminSubTabButton(
                        label = "Archived Chats",
                        icon = Icons.Filled.Forum,
                        isActive = activeSubTab == "ARCHIVED_CHATS",
                        modifier = Modifier.weight(1.0f)
                    ) { activeSubTab = "ARCHIVED_CHATS" }

                    AdminSubTabButton(
                        label = "AI Generates",
                        icon = Icons.Filled.AutoAwesome,
                        isActive = activeSubTab == "AI_IMAGES",
                        modifier = Modifier.weight(1.0f)
                    ) { activeSubTab = "AI_IMAGES" }
                }

                // Selected Screen viewport
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (activeSubTab) {
                        "AUDIT_LOGS" -> AuditLogsView(logs = logs)
                        "ARCHIVED_POSTS" -> ArchivedPostsView(posts = allPosts)
                        "ARCHIVED_CHATS" -> ArchivedChatsView(chats = allChats)
                        "AI_IMAGES" -> AiImagesAdminView(images = aiImages)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSubTabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
fun AuditLogsView(logs: List<AuditLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "CHRONOLOGICAL AUDIT REGISTER",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (logs.isEmpty()) {
            item {
                Text("No user audit logs compiled yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        } else {
            items(logs) { log ->
                val timeString = remember(log.timestamp) {
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    sdf.format(Date(log.timestamp))
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(

                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(
                                    containerColor = when (log.action) {
                                        "SYSTEM_SEEDING" -> MaterialTheme.colorScheme.primaryContainer
                                        "CLEAR_CHAT_HISTORY" -> MaterialTheme.colorScheme.errorContainer
                                        "POST_SOFT_DELETE" -> MaterialTheme.colorScheme.errorContainer
                                        "AI_IMAGE_GENERATED" -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.tertiaryContainer
                                    }
                                ) {
                                    Text(
                                        text = log.action,
                                        color = when (log.action) {
                                            "SYSTEM_SEEDING" -> MaterialTheme.colorScheme.onPrimaryContainer
                                            "CLEAR_CHAT_HISTORY" -> MaterialTheme.colorScheme.onErrorContainer
                                            "POST_SOFT_DELETE" -> MaterialTheme.colorScheme.onErrorContainer
                                            "AI_IMAGE_GENERATED" -> MaterialTheme.colorScheme.onSecondaryContainer
                                            else -> MaterialTheme.colorScheme.onTertiaryContainer
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 1.dp, horizontal = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = log.userName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(timeString, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.details, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Email: ${log.userEmail} • UserID: ${log.userId}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ArchivedPostsView(posts: List<com.example.data.Post>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "COMPLETE POST DATABASE ARCHIVE (NO DELETES)",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        val hiddenCount = posts.count { it.isHidden }
        item {
            Text(
                "Total Posts: ${posts.size} • Soft-Deleted/Hidden: $hiddenCount",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (posts.isEmpty()) {
            item {
                Text("No archived posts in database.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        } else {
            items(posts) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (post.isHidden) MaterialTheme.colorScheme.errorContainer.copy(alpha=0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                    border = BorderStroke(1.2.dp, if (post.isHidden) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(post.authorAvatar, fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp))
                                Text(post.authorName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            if (post.isHidden) {
                                Badge(containerColor = MaterialTheme.colorScheme.errorContainer) {
                                    Text("Deleted (Soft)", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                                }
                            } else {
                                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                    Text("Normal Feed", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(post.content, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        if (post.mediaUri != null) {
                            Text("Media Attachment URI: ${post.mediaUri}", color = MaterialTheme.colorScheme.secondary, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArchivedChatsView(chats: List<com.example.data.ChatMessage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "COMPLETE CONVERSATION DATA LEDGER (NO DELETES)",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        val hiddenCount = chats.count { it.isHiddenForSender || it.isHiddenForReceiver }
        item {
            Text(
                "Total Messages: ${chats.size} • Hidden from user UI: $hiddenCount",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (chats.isEmpty()) {
            item {
                Text("No communications registered in system database.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        } else {
            items(chats) { chat ->
                val isHidden = chat.isHiddenForSender || chat.isHiddenForReceiver
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isHidden) MaterialTheme.colorScheme.errorContainer.copy(alpha=0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                    border = BorderStroke(1.2.dp, if (isHidden) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row {
                                Text(text = chat.senderName, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = " ➡️ ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                Text(text = chat.receiverName, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            if (isHidden) {
                                Badge(containerColor = MaterialTheme.colorScheme.errorContainer) {
                                    Text("Archived/User Cleared", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = chat.content, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Type: ${chat.type.uppercase()} • SenderHidden: ${chat.isHiddenForSender} • ReceiverHidden: ${chat.isHiddenForReceiver}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiImagesAdminView(images: List<com.example.data.AiImage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "AI GENERATE CENTRAL DATABASE (NO DELETES)",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        item {
            Text(
                "Total Prompted Artworks: ${images.size}",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (images.isEmpty()) {
            item {
                Text("No prompted AI artworks compiled in database.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        } else {
            items(images) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.userName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Email: ${item.userEmail} • UserID: ${item.userId}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                            }
                            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            Text(sdf.format(Date(item.timestamp)), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Prompt: \"${item.prompt}\"",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        coil.compose.AsyncImage(
                            model = item.imageUrl,
                            contentDescription = "AI Graphic Art",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Image URL: ${item.imageUrl}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}
