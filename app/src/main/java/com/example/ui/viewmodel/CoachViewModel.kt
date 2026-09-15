package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AudioEngine
import com.example.data.ai.GeminiCoachService
import com.example.data.ai.SpeechAnalyzer
import com.example.data.appupdate.AppUpdateChecker
import com.example.data.appupdate.UpdateInstaller
import com.example.data.appupdate.UpdateState
import com.example.data.local.CoachDatabase
import com.example.data.model.Achievement
import com.example.data.model.ChatMessage
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.ExerciseResult
import com.example.data.model.ExerciseType
import com.example.data.model.PracticeSession
import com.example.data.model.UserAccount
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyWord
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
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

// ---- Authentication session exposed to the UI ----
sealed class AuthState {
    data object Loading : AuthState()
    data object LoggedOut : AuthState()
    data class LoggedIn(val username: String, val role: String) : AuthState() {
        val isAdmin: Boolean get() = role == AuthRepository.ROLE_ADMIN
    }
}

class CoachViewModel(application: Application) : AndroidViewModel(application) {

    private val database = CoachDatabase.getDatabase(application)
    private val repository = CoachRepository(database.coachDao())
    private val authRepository = AuthRepository(application, database.coachDao())
    private val geminiService = GeminiCoachService()
    val audioEngine = AudioEngine(application)

    // ---- Auth state ----
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authBusy = MutableStateFlow(false)
    val authBusy: StateFlow<Boolean> = _authBusy.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Accounts on this device — populated for the admin panel.
    private val _deviceAccounts = MutableStateFlow<List<UserAccount>>(emptyList())
    val deviceAccounts: StateFlow<List<UserAccount>> = _deviceAccounts.asStateFlow()

    // ---- In-app update state ----
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.ensureAdminSeeded()
            val session = authRepository.currentSession()
            _authState.value = session
                ?.let { AuthState.LoggedIn(it.first, it.second) }
                ?: AuthState.LoggedOut
            if (session != null) refreshAccountsForAdmin()
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authBusy.value = true
            _authError.value = null
            val result = authRepository.login(username, password)
            _authBusy.value = false
            when (result) {
                is AuthResult.Success -> {
                    authRepository.saveSession(result.username, result.role)
                    _authState.value = AuthState.LoggedIn(result.username, result.role)
                    refreshAccountsForAdmin()
                }
                is AuthResult.Error -> _authError.value = result.message
            }
        }
    }

    fun signup(username: String, password: String) {
        viewModelScope.launch {
            _authBusy.value = true
            _authError.value = null
            val result = authRepository.signup(username, password)
            _authBusy.value = false
            when (result) {
                is AuthResult.Success -> {
                    authRepository.saveSession(result.username, result.role)
                    _authState.value = AuthState.LoggedIn(result.username, result.role)
                    refreshAccountsForAdmin()
                }
                is AuthResult.Error -> _authError.value = result.message
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.LoggedOut
        _deviceAccounts.value = emptyList()
    }

    private suspend fun refreshAccountsForAdmin() {
        val state = _authState.value
        if (state is AuthState.LoggedIn && state.isAdmin) {
            _deviceAccounts.value = authRepository.allAccounts()
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            _updateState.value = AppUpdateChecker.check()
        }
    }

    /** Hides the update dialog until the next real check. */
    fun dismissUpdateForSession() {
        _updateState.value = UpdateState.Idle
    }

    fun downloadUpdate(info: com.example.data.appupdate.UpdateInfo) {
        val context = getApplication<Application>()
        _updateState.value = UpdateState.Downloading
        UpdateInstaller.downloadAndInstall(context, info)
    }

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
                text = "Welcome! I'm Aura, your AI Communication Coach. I've prepared today's 10-minute executive session focusing on clarity and filler-word reduction. How can I help you sharpen your delivery today?",
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
