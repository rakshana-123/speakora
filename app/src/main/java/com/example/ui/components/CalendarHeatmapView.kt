package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SuccessEmerald

@Composable
fun CalendarHeatmapView(
    activeDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13), // September 1st to 13th
    currentDay: Int = 13,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableStateOf<Int?>(13) }

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
                Column {
                    Text(
                        text = "Practice Heatmap",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "September 2026 • 13 consecutive days active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Less", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SuccessEmerald.copy(alpha = 0.5f)))
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SuccessEmerald))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("More", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday header
            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.width(32.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days grid: September 2026 starts on Tuesday (index 1)
            val totalDays = 30
            val offsetStart = 1 // Tuesday
            val gridCells = List(offsetStart) { -1 } + (1..totalDays).toList()

            for (week in gridCells.chunked(7)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { dayNumber ->
                        if (dayNumber == -1) {
                            Box(modifier = Modifier.size(32.dp))
                        } else {
                            val isActive = activeDays.contains(dayNumber)
                            val isSelected = selectedDay == dayNumber
                            val isFuture = dayNumber > currentDay

                            val cellColor = when {
                                isActive -> SuccessEmerald
                                isFuture -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(cellColor)
                                    .clickable { selectedDay = dayNumber },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected || isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    // Fill remaining empty cells in last row
                    if (week.size < 7) {
                        repeat(7 - week.size) {
                            Box(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            // Selected day info popup
            selectedDay?.let { day ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    val statusText = if (activeDays.contains(day)) {
                        "Sept $day, 2026: Completed daily workout (10 min • Speaking & Vocab • +95 XP)"
                    } else if (day > currentDay) {
                        "Sept $day, 2026: Upcoming planned practice"
                    } else {
                        "Sept $day, 2026: Rest day (Protected with Freeze Token)"
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
