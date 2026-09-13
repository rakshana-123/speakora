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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

data class SkillItem(
    val name: String,
    val score: Int,
    val deltaPercent: String,
    val isPositive: Boolean,
    val color: Color
)

@Composable
fun SkillScoreCards(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    val skills = listOf(
        SkillItem("Speaking", profile.speakingScore, "+14%", true, PrimaryIndigo),
        SkillItem("Vocabulary", profile.vocabularyScore, "+21%", true, SecondaryCyan),
        SkillItem("Pronunciation", profile.pronunciationScore, "+8%", true, Color(0xFF8B5CF6)),
        SkillItem("Listening", profile.listeningScore, "+12%", true, Color(0xFF0D9488)),
        SkillItem("Grammar", profile.grammarScore, "+9%", true, Color(0xFFD97706)),
        SkillItem("Writing", profile.writingScore, "+15%", true, Color(0xFFEC4899)),
        SkillItem("Confidence", profile.confidenceScore, "+11%", true, Color(0xFFE11D48)),
        SkillItem("Filler Words", 2, "-34%", true, SuccessEmerald) // reduction is positive
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Analytics",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "30-Day Skill Growth",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Based on continuous speaking telemetry",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Overall Score Ring pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${profile.overallScore} / 100",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2-column grid of skills
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in skills.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SkillMiniTile(
                            item = skills[i],
                            modifier = Modifier.weight(1f)
                        )
                        if (i + 1 < skills.size) {
                            SkillMiniTile(
                                item = skills[i + 1],
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillMiniTile(
    item: SkillItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = "Trend",
                        tint = SuccessEmerald,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = item.deltaPercent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessEmerald,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${item.score}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (item.score / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = item.color,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}
