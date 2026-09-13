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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessEmerald

@Composable
fun TodayWorkoutCard(
    targetMinutes: Int,
    exercises: List<Exercise>,
    completedIds: Set<String>,
    onStartWorkout: () -> Unit,
    onSelectExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = exercises.count { completedIds.contains(it.id) }
    val progress = if (exercises.isNotEmpty()) completedCount.toFloat() / exercises.size else 0f
    val isAllCompleted = exercises.isNotEmpty() && completedCount == exercises.size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_workout_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Title and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Communication Workout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isAllCompleted) "All completed for today! Great job." else "$completedCount of ${exercises.size} modules finished",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$targetMinutes min",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isAllCompleted) SuccessEmerald else PrimaryIndigo,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exercise items checklist
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                exercises.forEach { exercise ->
                    val isDone = completedIds.contains(exercise.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onSelectExercise(exercise) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isDone) "Completed" else "Pending",
                                tint = if (isDone) SuccessEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = exercise.category.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = exercise.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        Text(
                            text = "+${exercise.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start CTA Button
            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_today_practice_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAllCompleted) SuccessEmerald else PrimaryIndigo
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start practice",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAllCompleted) "Review Today's Workout" else "Start Today's Practice",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
