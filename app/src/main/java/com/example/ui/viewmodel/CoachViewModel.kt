package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AudioEngine
import com.example.data.ai.GeminiCoachService
import com.example.data.ai.SpeechAnalyzer
import com.example.data.local.CoachDatabase
import com.example.data.model.Achievement
import com.example.data.model.ChatMessage
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.ExerciseResult
import com.example.data.model.ExerciseType
import com.example.data.model.PracticeSession
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyWord
import com.example.data.repository.CoachRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoachViewModel(application: Application) : AndroidViewModel(application) {

    private val database = CoachDatabase.getDatabase(application)
    private val repository = CoachRepository(database.coachDao())
    private val geminiService = GeminiCoachService()
    val audioEngine = AudioEngine(application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val vocabularyList: StateFlow<List<VocabularyWord>> = repository.vocabularyList
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val practiceSessions: StateFlow<List<PracticeSession>> = repository.practiceSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val achievements: StateFlow<List<Achievement>> = repository.achievements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Daily Workout State
    private val _todayWorkout = MutableStateFlow<List<Exercise>>(emptyList())
    val todayWorkout: StateFlow<List<Exercise>> = _todayWorkout.asStateFlow()

    private val _completedWorkoutIds = MutableStateFlow<Set<String>>(emptySet())
    val completedWorkoutIds: StateFlow<Set<String>> = _completedWorkoutIds.asStateFlow()

    // Active Exercise Player State
    private val _activeExercise = MutableStateFlow<Exercise?>(null)
    val activeExercise: StateFlow<Exercise?> = _activeExercise.asStateFlow()

    private val _exerciseResult = MutableStateFlow<ExerciseResult?>(null)
    val exerciseResult: StateFlow<ExerciseResult?> = _exerciseResult.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ExerciseCategory?>(null)
    val selectedCategory: StateFlow<ExerciseCategory?> = _selectedCategory.asStateFlow()

    // Recording & Audio State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _audioAmplitudes = MutableStateFlow<List<Float>>(emptyList())
    val audioAmplitudes: StateFlow<List<Float>> = _audioAmplitudes.asStateFlow()

    // AI Coach Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Roleplay messages state
    private val _roleplayMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val roleplayMessages: StateFlow<List<Pair<String, String>>> = _roleplayMessages.asStateFlow()

    // Toast / Feedback message
    private val _feedbackNotice = MutableStateFlow<String?>(null)
    val feedbackNotice: StateFlow<String?> = _feedbackNotice.asStateFlow()

    private var recordingJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeDatabase()
            refreshTodayWorkout()
            initializeDefaultChat()
        }
    }

    fun refreshTodayWorkout() {
        viewModelScope.launch {
            val minutes = userProfile.value.dailyMinutes
            val workout = repository.getTodayWorkout(minutes)
            _todayWorkout.value = workout
        }
    }

    private fun initializeDefaultChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = "msg_1",
                senderName = "Aura (AI Coach)",
                text = "Hello Alex! I'm Aura, your AI Communication Coach. I've prepared today's 10-minute executive session focusing on clarity and filler-word reduction. How can I help you sharpen your delivery today?",
                isFromUser = false,
                suggestions = listOf(
                    "Give me today's speaking practice",
                    "Practice an interview with me",
                    "How do I eliminate filler words?",
                    "Review my email draft"
                )
            )
        )
    }

    fun setCategoryFilter(category: ExerciseCategory?) {
        _selectedCategory.value = category
    }

    fun selectExercise(exercise: Exercise) {
        _activeExercise.value = exercise
        _exerciseResult.value = null
        _roleplayMessages.value = if (exercise.type == ExerciseType.ROLEPLAY_CONVERSATION) {
            listOf(Pair("AI Counterpart", exercise.promptContent))
        } else {
            emptyList()
        }
    }

    fun closeExercise() {
        _activeExercise.value = null
        _exerciseResult.value = null
        stopRecording()
    }

    fun startRecording() {
        _isRecording.value = true
        _recordingSeconds.value = 0
        _audioAmplitudes.value = emptyList()

        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(200)
                _recordingSeconds.value += 1
                val randomAmp = (0.2f + (Math.random().toFloat() * 0.8f))
                _audioAmplitudes.value = (_audioAmplitudes.value + randomAmp).takeLast(28)
            }
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
    }

    fun finishSpeakingExercise(transcript: String? = null) {
        stopRecording()
        val currentEx = _activeExercise.value ?: return
        val effectiveTranscript = if (!transcript.isNullOrBlank()) {
            transcript
        } else {
            currentEx.sampleIdealAnswer.ifBlank {
                "In my perspective, we must align our strategic roadmap with verified customer telemetry. We cut onboarding latency by 35% and continue to prioritize core user experience."
            }
        }

        val duration = _recordingSeconds.value / 5 // convert 200ms ticks to seconds
        val result = SpeechAnalyzer.analyzeSpeech(
            transcript = effectiveTranscript,
            durationSeconds = if (duration > 5) duration else 35,
            exerciseId = currentEx.id,
            baseXp = currentEx.xpReward
        )

        _exerciseResult.value = result
        _completedWorkoutIds.value = _completedWorkoutIds.value + currentEx.id

        viewModelScope.launch {
            repository.submitExerciseResult(currentEx, result)
        }
    }

    fun submitQuizChoice(selectedOption: String) {
        val currentEx = _activeExercise.value ?: return
        val isCorrect = selectedOption.equals(currentEx.correctAnswer, ignoreCase = true)
        val score = if (isCorrect) 95 else 60
        val earnedXp = if (isCorrect) currentEx.xpReward else (currentEx.xpReward / 2)

        val result = ExerciseResult(
            exerciseId = currentEx.id,
            score = score,
            userResponse = selectedOption,
            feedback = if (isCorrect) {
                "Correct! ${currentEx.explanation}"
            } else {
                "Not quite. ${currentEx.explanation}"
            },
            strengths = if (isCorrect) listOf("Accurate contextual nuance detection", "Strong comprehension") else emptyList(),
            areasToImprove = if (!isCorrect) listOf("Review explanation: ${currentEx.explanation}") else emptyList(),
            xpEarned = earnedXp
        )

        _exerciseResult.value = result
        _completedWorkoutIds.value = _completedWorkoutIds.value + currentEx.id

        viewModelScope.launch {
            repository.submitExerciseResult(currentEx, result)
            if (currentEx.category == ExerciseCategory.VOCABULARY) {
                repository.updateVocabularyMastery(currentEx.id, isCorrect)
            }
        }
    }

    fun submitWritingResponse(userDraft: String) {
        val currentEx = _activeExercise.value ?: return
        _isAiThinking.value = true

        viewModelScope.launch {
            val (critique, tone, rewritten) = geminiService.evaluateWriting(currentEx.instructions, userDraft)
            _isAiThinking.value = false

            val result = ExerciseResult(
                exerciseId = currentEx.id,
                score = 88,
                userResponse = userDraft,
                feedback = "$critique\n\nDetected Tone: $tone\n\nPolished Version:\n\"$rewritten\"",
                strengths = listOf("Direct and professional communication", "Tone: $tone"),
                areasToImprove = listOf("Consider using the suggested rewritten structure for high-stakes executive clarity."),
                xpEarned = currentEx.xpReward
            )

            _exerciseResult.value = result
            _completedWorkoutIds.value = _completedWorkoutIds.value + currentEx.id
            repository.submitExerciseResult(currentEx, result)
        }
    }

    fun submitRoleplayReply(userReply: String) {
        val currentEx = _activeExercise.value ?: return
        val updatedHistory = _roleplayMessages.value + Pair("You", userReply)
        _roleplayMessages.value = updatedHistory
        _isAiThinking.value = true

        viewModelScope.launch {
            val (counterpartReply, coachTip) = geminiService.evaluateRoleplayTurn(
                scenarioPrompt = currentEx.promptContent,
                conversationHistory = updatedHistory,
                userReply = userReply
            )
            _isAiThinking.value = false
            _roleplayMessages.value = updatedHistory + Pair("AI Counterpart", counterpartReply)
            _feedbackNotice.value = "Coach Tip: $coachTip"
        }
    }

    fun completeRoleplayScenario() {
        val currentEx = _activeExercise.value ?: return
        val result = ExerciseResult(
            exerciseId = currentEx.id,
            score = 92,
            userResponse = "Completed ${_roleplayMessages.value.size / 2} conversation turns",
            feedback = "Great diplomacy! You navigated stakeholder resistance while maintaining executive composure and proposing practical compromises.",
            strengths = listOf("Assertive yet empathetic tone", "Clear structured solution proposal"),
            areasToImprove = listOf("Continue reinforcing agreement with written follow-up notes."),
            xpEarned = currentEx.xpReward
        )
        _exerciseResult.value = result
        _completedWorkoutIds.value = _completedWorkoutIds.value + currentEx.id

        viewModelScope.launch {
            repository.submitExerciseResult(currentEx, result)
        }
    }

    fun sendCoachChat(message: String) {
        if (message.isBlank()) return
        val newHistory = _chatMessages.value + ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "You",
            text = message,
            isFromUser = true
        )
        _chatMessages.value = newHistory
        _isAiThinking.value = true

        viewModelScope.launch {
            val coachReply = geminiService.chatWithCoach(
                userMessage = message,
                userLevel = userProfile.value.communicationLevel,
                goal = userProfile.value.mainGoal
            )
            _isAiThinking.value = false
            _chatMessages.value = newHistory + ChatMessage(
                id = "msg_${System.currentTimeMillis()}",
                senderName = "Aura (AI Coach)",
                text = coachReply,
                isFromUser = false
            )
        }
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            val success = repository.useStreakFreeze()
            _feedbackNotice.value = if (success) {
                "Streak freeze activated! Your 18-day streak is protected for today."
            } else {
                "No streak freeze tokens available."
            }
        }
    }

    fun recoverStreak() {
        viewModelScope.launch {
            val success = repository.recoverStreakChallenge()
            _feedbackNotice.value = if (success) {
                "Recovery challenge completed! Your streak momentum is restored."
            } else {
                "Streak recovery failed."
            }
        }
    }

    fun updateProfilePreferences(
        goal: String,
        level: String,
        dailyMinutes: Int,
        preferredTime: String,
        nativeLang: String,
        learningLang: String
    ) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(
                mainGoal = goal,
                communicationLevel = level,
                dailyMinutes = dailyMinutes,
                preferredTime = preferredTime,
                nativeLanguage = nativeLang,
                learningLanguage = learningLang
            )
            repository.updateProfile(updated)
            refreshTodayWorkout()
            _feedbackNotice.value = "Profile preferences saved successfully!"
        }
    }

    fun clearFeedbackNotice() {
        _feedbackNotice.value = null
    }

    fun speakText(text: String) {
        audioEngine.speak(text)
    }

    fun stopSpeaking() {
        audioEngine.stop()
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.shutdown()
        recordingJob?.cancel()
    }
}
