package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.StreakFlameLight

@Composable
fun StreakCard(
    currentStreak: Int,
    bestStreak: Int,
    freezeTokens: Int,
    isFrozenToday: Boolean,
    onUseFreeze: () -> Unit,
    onOpenRecovery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFEA580C),
                            Color(0xFFF97316),
                            Color(0xFFFB923C)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak flame",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "$currentStreak Day Streak",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "Consistent daily communicator",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Best Streak Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Best streak",
                            tint = Color(0xFFFEF08A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Best: $bestStreak",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom row: Streak freeze and protection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AcUnit,
                            contentDescription = "Freeze tokens",
                            tint = Color.White.copy(alpha = 0.95f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFrozenToday) "Protected Today" else "$freezeTokens Freeze${if (freezeTokens != 1) "s" else ""} Available",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    if (!isFrozenToday && freezeTokens > 0) {
                        OutlinedButton(
                            onClick = onUseFreeze,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Color.White, Color.White))
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("freeze_streak_button")
                        ) {
                            Text("Freeze Streak", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else if (isFrozenToday) {
                        Text(
                            text = "Active Shield",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFEF08A)
                        )
                    }
                }
            }
        }
    }
}
