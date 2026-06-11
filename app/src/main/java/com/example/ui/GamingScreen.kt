package com.example.ui

import com.example.ui.theme.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.GameLobby

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamingScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lobbies by viewModel.allGameLobbies.collectAsState()
    val activeLobby by viewModel.activeLobby.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var lobbyNameInput by remember { mutableStateOf("") }
    var matchFormatSelected by remember { mutableStateOf("1vs1") }
    val formats = listOf("1vs1", "1vs2", "2vs2", "3vs3", "5vs5")

    Box(modifier = modifier.fillMaxSize().background(Color.Transparent)) {
        if (activeLobby == null) {
            // Lobbies Explorer & Creator
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "MULTIPLAYER GAME HALL",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Instant-play retro HTML5 battle lobbies. No installs required.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Premium Circular Game Launchpad Launcher Icons Row maintaining the RN aesthetic
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "QUICK START BATTLEGROUND",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Launcher 1: 1vs1 Duel (Luxurious Gold Theme)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        lobbyNameInput = "Royale 1vs1 Duel"
                                        matchFormatSelected = "1vs1"
                                        viewModel.createGameLobby(lobbyNameInput, matchFormatSelected)
                                        lobbyNameInput = ""
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(
                                            androidx.compose.ui.graphics.Brush.radialGradient(
                                                colors = listOf(Color(0xFFFFF2AF), Color(0xFFD4AF37))
                                            )
                                        )
                                        .border(
                                            BorderStroke(2.dp, Color(0xFFFFF8D6)),
                                            androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalFireDepartment,
                                        contentDescription = "1vs1 Duel",
                                        tint = Color(0xFF030D1D),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "1vs1 Duel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Launcher 2: Team Battle (Metallic Blue Theme)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        lobbyNameInput = "Nexus Team Battle"
                                        matchFormatSelected = "2vs2"
                                        viewModel.createGameLobby(lobbyNameInput, matchFormatSelected)
                                        lobbyNameInput = ""
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(
                                            androidx.compose.ui.graphics.Brush.radialGradient(
                                                colors = listOf(Color(0xFF86BCFF), Color(0xFF1E60B4))
                                            )
                                        )
                                        .border(
                                            BorderStroke(2.dp, Color(0xFFCBE3FF)),
                                            androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.People,
                                        contentDescription = "Team Battle",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Team Battle",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Launcher 3: Multiplayer Arena (Hybrid Theme)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        lobbyNameInput = "World Coliseum Arena"
                                        matchFormatSelected = "5vs5"
                                        viewModel.createGameLobby(lobbyNameInput, matchFormatSelected)
                                        lobbyNameInput = ""
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(
                                            androidx.compose.ui.graphics.Brush.radialGradient(
                                                colors = listOf(Color(0xFFE5A93B), Color(0xFF1C3A6E))
                                            )
                                        )
                                        .border(
                                            BorderStroke(2.dp, Color(0xFFD4AF37)),
                                            androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SportsEsports,
                                        contentDescription = "Multiplayer Arena",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Multiplayer Arena",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Lobby Creator Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "PROVISION NEW SECURE ROOM",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = lobbyNameInput,
                                onValueChange = { lobbyNameInput = it },
                                placeholder = { Text("E.g., Kazam Cyber Clash", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("SELECT FIGHT LAYOUT SLOT CONFIGURATION", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Choice Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                formats.forEach { mode ->
                                    val isSel = matchFormatSelected == mode
                                    Card(
                                        modifier = Modifier
                                            .weight(1.0f)
                                            .clickable { matchFormatSelected = mode },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = mode,
                                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.createGameLobby(lobbyNameInput, matchFormatSelected)
                                    lobbyNameInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Inaugurate Lobby Board", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Lobbies Lists
                item {
                    Text(
                        text = "ACTIVE ROOM REGISTERS",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                if (lobbies.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No open game rooms yet. Design yours above!", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(lobbies) { lob ->
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectLobby(lob) }
                                .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                            headlineContent = { Text(lob.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                            supportingContent = {
                                Column {
                                    Text("Host: ${lob.hostName} • Slotted Limit: ${lob.format}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                    if (lob.winnerName != null) {
                                        Text("🏆 Game Winner: ${lob.winnerName}", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(18.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (lob.status == "finished") Icons.Filled.EmojiEvents else Icons.Filled.Gamepad,
                                        contentDescription = null,
                                        tint = if (lob.status == "finished") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Badge(
                                        containerColor = when (lob.status) {
                                            "waiting" -> MaterialTheme.colorScheme.secondaryContainer
                                            "playing" -> MaterialTheme.colorScheme.errorContainer
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                    ) {
                                        Text(
                                            text = lob.status.uppercase(),
                                            color = when (lob.status) {
                                                "waiting" -> MaterialTheme.colorScheme.onSecondaryContainer
                                                "playing" -> MaterialTheme.colorScheme.onErrorContainer
                                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (lob.hostId == currentUser?.id) {
                                        IconButton(onClick = { viewModel.dismissLobby(lob.lobbyId) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        } else {
            // Screen is active within a Lobby
            val lobby = activeLobby!!
            val isHost = lobby.hostId == currentUser?.id

            Column(modifier = Modifier.fillMaxSize()) {
                // Lobby Header bar
                TopAppBar(
                    title = {
                        Column {
                            Text(lobby.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Room ID: ${lobby.lobbyId} • Layout Config: ${lobby.format}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.leaveActiveLobby() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Leave", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                if (lobby.status == "playing") {
                    // HTML5 Instant Play WebView Frame (No external download container)
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    webViewClient = WebViewClient()
                                    addJavascriptInterface(object {
                                        @JavascriptInterface
                                        fun onMatchFinished(winner: String) {
                                            // Trigger results highlights in android thread
                                            post {
                                                viewModel.handleMatchFinished(winner)
                                            }
                                        }
                                    }, "KazamApp")
                                    // Load local game file preloaded in assets
                                    loadUrl("file:///android_asset/game.html?p1=${currentUser?.name ?: "Challenger"}&p2=Cosmic_AI")
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Waiting Lobby / Match Finish ledger review
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Winner banner if completed
                        if (lobby.status == "finished") {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(28.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Filled.EmojiEvents, contentDescription = "Trophy Winner", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "MATCH COMPLETE",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "${lobby.winnerName ?: "Player"} WINS THE DUEL!",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Permanent Golden Trophy has been permanently registered to ${lobby.winnerName ?: "their"} ledger securely.",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Team Rosters Layout
                        item {
                            Text("ACTIVE SLOT PLACEMENT MAPPINGS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, letterSpacing = 1.sp)
                        }

                        // Team 1 Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("TEAM BLUE SLOTS", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    lobby.team1Members.split(",").forEach { member ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Filled.AccountBox, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(member.trim(), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Team 2 Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("TEAM ORANGE SLOTS", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    lobby.team2Members.split(",").forEach { member ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Filled.AccountBox, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(member.trim(), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // HOST EXCLUSIVE MATCH UTILITIES (Requirement 4)
                        if (isHost && lobby.status == "waiting") {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.RoomPreferences, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("EXCLUSIVE HOST SLOTS PANEL", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, letterSpacing = 1.sp)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Team Swap (Exclusive Permission)
                                        Button(
                                            onClick = { viewModel.swapLobbyPlacements() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Icon(Icons.Filled.SwapHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Toggle Team Swap Slots", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Inject Simulated Contender Slots
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = { viewModel.addVirtualPlayerToSlots(1, "Fareed (Invited)") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                                modifier = Modifier.weight(1.0f),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text("+ Slot 1", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { viewModel.addVirtualPlayerToSlots(2, "Mr Kazam (Invited)") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                                modifier = Modifier.weight(1.0f),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text("+ Slot 2", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Commencing match
                        if (lobby.status != "finished") {
                            item {
                                Button(
                                    onClick = { viewModel.launchMatch() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("COMMENCE INSTANT MATCH", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            item {
                                Button(
                                    onClick = { viewModel.leaveActiveLobby() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("RETURN TO MULTIPLAYER HALL", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
