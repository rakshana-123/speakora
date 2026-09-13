package com.example.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SeedData
import com.example.data.model.CommunityRoom
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.CoachViewModel

@Composable
fun ChallengesScreen(
    viewModel: CoachViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("challenges_screen")
    ) {
        Text(
            text = "Social & Challenges",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Accountability duels, streak shields, and live practice rooms",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Streak Resilience & Recovery Challenge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StreakFlame.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = "Recovery", tint = StreakFlame, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Streak Protection & Recovery",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Your streak ended, but your progress didn't",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AcUnit, contentDescription = "Freeze", tint = SecondaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${userProfile.freezeTokens} Freeze Token(s) available",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { viewModel.useStreakFreeze() },
                        enabled = userProfile.freezeTokens > 0 && !userProfile.streakFrozenToday,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text(if (userProfile.streakFrozenToday) "Active" else "Use Freeze")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { viewModel.recoverStreak() },
                    modifier = Modifier.fillMaxWidth().testTag("recover_streak_challenge_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFlame)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Complete 15-Min Streak Recovery Challenge", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Weekly Leaderboard
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Leaderboard", tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Weekly Cohort Leaderboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Division 4",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val leaders = listOf(
                    Triple("1", "Maya Lin", 3420),
                    Triple("2", "Alex Chen (You)", userProfile.xp),
                    Triple("3", "David Kim", 2600),
                    Triple("4", "Priya Sharma", 2450)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    leaders.forEach { (rank, name, xp) ->
                        val isUser = name.contains("You")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isUser) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#$rank",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rank == "1") Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isUser) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                            Text(
                                text = "$xp XP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Live Community Practice Rooms
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SecondaryCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = "Community", tint = SecondaryCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Live Community Practice Rooms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Peer feedback, live stage pitches & impromptu clubs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SeedData.communityRooms.forEach { room ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SuccessEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${room.participantsCount} online • Hosted by ${room.hostName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = room.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = room.topic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            Button(
                                onClick = { /* Connect to room */ },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Join Stage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
