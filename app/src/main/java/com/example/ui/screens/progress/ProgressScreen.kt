package com.example.ui.screens.progress

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.CalendarHeatmapView
import com.example.ui.components.SkillScoreCards
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.CoachViewModel

@Composable
fun ProgressScreen(
    viewModel: CoachViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val practiceSessions by viewModel.practiceSessions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("progress_screen")
    ) {
        Text(
            text = "Communication Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Quantitative speech telemetry and habit milestones",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Key Speech Telemetry Metrics Row (WPM, Fillers, Total Workouts)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // WPM Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = "WPM", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${userProfile.avgWpm}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Avg WPM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Optimal 125-155", style = MaterialTheme.typography.labelSmall, color = SuccessEmerald, fontSize = 10.sp)
                }
            }

            // Fillers Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SuccessEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Fillers", tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${userProfile.avgFillerWordsPerMin}/m", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Filler Words", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "-34% this month", style = MaterialTheme.typography.labelSmall, color = SuccessEmerald, fontSize = 10.sp)
                }
            }

            // Total Sessions Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SecondaryCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = "Workouts", tint = SecondaryCyan, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${userProfile.totalWorkoutsCompleted}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Workouts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "420+ minutes", style = MaterialTheme.typography.labelSmall, color = PrimaryIndigo, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 30-Day Activity Calendar Heatmap
        CalendarHeatmapView()

        Spacer(modifier = Modifier.height(16.dp))

        // AI Recommendation Engine Insights
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
                            .background(PrimaryIndigo.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Recommendation", tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Adaptive Recommendation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Why this workout was generated for you",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Based on your last 5 sessions, your speaking fluency increased by 11%, but your vocabulary score (75%) remains your lowest discipline. Today's session prioritizes strategic executive terminology ('pragmatic', 'leverage', 'conciliatory') combined with impromptu pushback handling.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skill Scores Card
        SkillScoreCards(profile = userProfile)

        Spacer(modifier = Modifier.height(24.dp))
    }
}
