package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core ViewModel Initialization
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                val allUsers by viewModel.allUsers.collectAsState()

                var activeTab by remember { mutableStateOf("FEED") } // "FEED", "CHATS", "GAMING", "ADMIN"
                var profileDropdownExpanded by remember { mutableStateOf(false) }

                // Sophisticated royal-to-gold gradient
                val premiumGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF030D1E), // Midnight Royal Blue
                        Color(0xFF071733), // Deep Blue Navy
                        Color(0xFF13233D), // Muted Blue Steel
                        Color(0xFF382B0A)  // Warm Gold Dust
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(premiumGradient)
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent, // Let our premium gradient shine through
                        topBar = {
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                tonalElevation = 6.dp,
                                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Title Logo with intertwined RN monogram integration
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .border(
                                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                                    androidx.compose.foundation.shape.CircleShape
                                                )
                                        ) {
                                            coil.compose.AsyncImage(
                                                model = com.example.R.drawable.rn_app_icon_1781198528609,
                                                contentDescription = "RN Monogram",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "MR KAZAM",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp,
                                                letterSpacing = 2.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "PREMIUM HYBRID PROTOCOL",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp,
                                                letterSpacing = 1.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Profile Switcher Dropdown (UX helper to test different client states)
                                    Box {
                                        Card(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                                .align(Alignment.CenterEnd)
                                                .clickable { profileDropdownExpanded = true },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = currentUser?.avatarEmoji ?: "👤",
                                                    fontSize = 18.sp,
                                                    modifier = Modifier.padding(end = 6.dp)
                                                )
                                                Column {
                                                    Text(
                                                        text = currentUser?.name ?: "Loading...",
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "🏆 Trophies: ${currentUser?.trophies ?: 0}",
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowDropDown,
                                                    contentDescription = "Expand Swapper",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = profileDropdownExpanded,
                                            onDismissRequest = { profileDropdownExpanded = false },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = "SWAP ACTIVE SESSION PROFILE",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                            allUsers.forEach { user ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(user.avatarEmoji, fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                                            Column {
                                                                Text(user.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                                                                Text(if (user.isAdmin) "Master Admin Role" else "Standard Platform Member", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                                            }
                                                        }
                                                    },
                                                    onClick = {
                                                        viewModel.switchCurrentUser(user)
                                                        profileDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            ) {
                                NavigationBarItem(
                                    selected = activeTab == "FEED",
                                    label = { Text("Feed", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = { Icon(Icons.Filled.DynamicFeed, contentDescription = "Feed") },
                                    onClick = { activeTab = "FEED" },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )

                                NavigationBarItem(
                                    selected = activeTab == "CHATS",
                                    label = { Text("Chats", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = { Icon(Icons.Filled.Security, contentDescription = "Chats") },
                                    onClick = { activeTab = "CHATS" },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )

                                NavigationBarItem(
                                    selected = activeTab == "GAMING",
                                    label = { Text("Games", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = { Icon(Icons.Filled.SportsEsports, contentDescription = "Gaming") },
                                    onClick = { activeTab = "GAMING" },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )

                                NavigationBarItem(
                                    selected = activeTab == "ADMIN",
                                    label = { Text("Audits", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin") },
                                    onClick = { activeTab = "ADMIN" },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            when (activeTab) {
                                "FEED" -> FeedScreen(viewModel = viewModel)
                                "CHATS" -> ChatsScreen(viewModel = viewModel)
                                "GAMING" -> GamingScreen(viewModel = viewModel)
                                "ADMIN" -> AdminPanelScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
