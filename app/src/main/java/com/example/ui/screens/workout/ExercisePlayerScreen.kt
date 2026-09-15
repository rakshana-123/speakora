package com.example.ui.screens.workout

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.ExerciseType
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.CoachViewModel

@Composable
fun ExercisePlayerScreen(
    exercise: Exercise,
    viewModel: CoachViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exerciseResult by viewModel.exerciseResult.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val amplitudes by viewModel.audioAmplitudes.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val roleplayMessages by viewModel.roleplayMessages.collectAsState()

    // Real microphone capture requires a runtime permission grant.
    val context = androidx.compose.ui.platform.LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }

    var userSpeechInput by remember { mutableStateOf("") }
    var userWritingInput by remember { mutableStateOf("") }
    var selectedQuizOption by remember { mutableStateOf<String?>(null) }
    var roleplayReplyText by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("exercise_player_screen")
    ) { innerPadding ->
        if (exerciseResult != null) {
            // Exercise Result Celebration Screen
            val res = exerciseResult!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SuccessEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SuccessEmerald,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Exercise Completed!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "+${res.xpEarned} XP Earned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Score Ring Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${res.score}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (res.score >= 80) SuccessEmerald else PrimaryIndigo
                        )
                        Text(
                            text = "Score / 100",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (res.wpm > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${res.wpm} WPM",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Cadence",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${res.fillerCount}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.fillerCount == 0) SuccessEmerald else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Filler Words",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Feedback summary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = res.feedback,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (res.strengths.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Strengths",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessEmerald
                                )
                                res.strengths.forEach { s ->
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = SuccessEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = s,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        if (res.areasToImprove.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Focus for Next Session",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                res.areasToImprove.forEach { tip ->
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "•",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = tip,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("result_continue_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            // Interactive Exercise Play Mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Row
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
                                imageVector = getCategoryIcon(exercise.category),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = exercise.category.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Text(
                                text = exercise.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_exercise_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close exercise")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Prompt & Instructions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "INSTRUCTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exercise.instructions,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "PROMPT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exercise.promptContent,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // DYNAMIC CONTENT BASED ON EXERCISE TYPE
                when (exercise.type) {
                    ExerciseType.RECORDING, ExerciseType.PRONUNCIATION_REPEAT -> {
                        // Pronunciation / Speaking recording view
                        if (exercise.audioScript.isNotBlank()) {
                            OutlinedButton(
                                onClick = { viewModel.speakText(exercise.audioScript) },
                                modifier = Modifier.fillMaxWidth().testTag("listen_audio_script_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Play Audio")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Listen to Model Pronunciation (Native Cadence)", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Audio Waveform
                        AudioWaveformVisualizer(
                            isRecording = isRecording,
                            amplitudes = amplitudes
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Timer text (recordingSeconds is real elapsed seconds)
                        val seconds = recordingSeconds
                        val mins = seconds / 60
                        val remSecs = seconds % 60
                        Text(
                            text = String.format("%02d:%02d", mins, remSecs),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mic Toggle Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (isRecording) {
                                        viewModel.stopRecording()
                                    } else if (hasMicPermission) {
                                        viewModel.startRecording()
                                    } else {
                                        micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(70.dp)
                                    .testTag("toggle_recording_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecording) Color(0xFFEF4444) else PrimaryIndigo
                                )
                            ) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = if (isRecording) "Stop" else "Record",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Speech Transcript / Input (Edit or speak freely):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = userSpeechInput,
                            onValueChange = { userSpeechInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("speech_transcript_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.finishSpeakingExercise(userSpeechInput)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("analyze_speech_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Text("Analyze Speech & Finish", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    ExerciseType.MULTIPLE_CHOICE, ExerciseType.FILL_BLANK, ExerciseType.LISTENING_COMPREHENSION -> {
                        if (exercise.audioScript.isNotBlank()) {
                            OutlinedButton(
                                onClick = { viewModel.speakText(exercise.audioScript) },
                                modifier = Modifier.fillMaxWidth().testTag("play_listening_audio_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Play Audio")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Play Audio Passage", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            exercise.options.forEach { option ->
                                val isSelected = selectedQuizOption == option
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { selectedQuizOption = option }
                                        .padding(16.dp)
                                        .testTag("quiz_option_$option")
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                selectedQuizOption?.let {
                                    viewModel.submitQuizChoice(it)
                                }
                            },
                            enabled = selectedQuizOption != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_quiz_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Text("Submit Answer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    ExerciseType.WRITING_RESPONSE -> {
                        OutlinedTextField(
                            value = userWritingInput,
                            onValueChange = { userWritingInput = it },
                            placeholder = { Text("Draft your response here...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("writing_response_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isAiThinking) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Analyzing clarity, tone, and grammar...")
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (userWritingInput.isNotBlank()) {
                                        viewModel.submitWritingResponse(userWritingInput)
                                    }
                                },
                                enabled = userWritingInput.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_writing_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                            ) {
                                Text("Get AI Tone & Grammar Review", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    ExerciseType.ROLEPLAY_CONVERSATION -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            roleplayMessages.forEach { (sender, text) ->
                                val isUser = sender == "You"
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isUser) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .padding(14.dp)
                                            .fillMaxWidth(0.85f)
                                    ) {
                                        Column {
                                            Text(
                                                text = sender,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUser) Color.White.copy(alpha = 0.8f) else PrimaryIndigo
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isAiThinking) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Counterpart is typing...", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = roleplayReplyText,
                                onValueChange = { roleplayReplyText = it },
                                placeholder = { Text("Reply to stakeholder...", fontSize = 14.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("roleplay_reply_input"),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (roleplayReplyText.isNotBlank()) {
                                        viewModel.submitRoleplayReply(roleplayReplyText)
                                        roleplayReplyText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryIndigo)
                                    .testTag("send_roleplay_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.completeRoleplayScenario() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("finish_roleplay_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald)
                        ) {
                            Text("Complete Roleplay Scenario", fontWeight = FontWeight.Bold)
                        }
                    }

                    else -> {
                        Button(
                            onClick = {
                                viewModel.finishSpeakingExercise(userSpeechInput)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Complete Drill")
                        }
                    }
                }
            }
        }
    }
}
