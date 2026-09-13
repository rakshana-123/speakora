package com.example.data.ai

import com.example.data.model.ExerciseResult

object SpeechAnalyzer {
    private val FILLER_WORDS = listOf(
        "um", "uh", "like", "you know", "sort of", "kind of",
        "basically", "actually", "literally", "i mean", "so yeah"
    )

    fun analyzeSpeech(
        transcript: String,
        durationSeconds: Int,
        exerciseId: String,
        baseXp: Int = 30
    ): ExerciseResult {
        val cleanText = transcript.trim()
        val words = cleanText.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size

        // Calculate WPM (safe division)
        val validDuration = if (durationSeconds > 0) durationSeconds else 30
        val minutes = validDuration.toDouble() / 60.0
        val wpm = if (minutes > 0) ((wordCount / minutes).toInt()) else 120

        // Filler words detection
        val lowerText = cleanText.lowercase()
        val detectedFillers = mutableListOf<String>()
        var fillerCount = 0

        for (filler in FILLER_WORDS) {
            val pattern = "\\b$filler\\b".toRegex()
            val matches = pattern.findAll(lowerText).count()
            if (matches > 0) {
                fillerCount += matches
                repeat(matches) { detectedFillers.add(filler) }
            }
        }

        // Pacing score (ideal conversational pace is 125-155 WPM)
        val paceScore = when {
            wpm in 125..155 -> 95
            wpm in 110..124 || wpm in 156..170 -> 85
            wpm in 90..109 || wpm in 171..190 -> 72
            else -> 60
        }

        // Filler penalty: 5 points off per filler word up to 40 max penalty
        val fillerPenalty = (fillerCount * 6).coerceAtMost(40)
        val clarityScore = (98 - fillerPenalty).coerceIn(50, 98)

        // Vocabulary complexity: check for words > 6 characters
        val advancedWords = words.count { it.length > 7 }
        val vocabScore = (70 + (advancedWords * 4)).coerceIn(60, 96)

        // Fluency score
        val fluencyScore = ((paceScore * 0.5) + (clarityScore * 0.5)).toInt()
        val overallScore = ((fluencyScore * 0.4) + (clarityScore * 0.3) + (vocabScore * 0.3)).toInt()

        // Construct targeted strengths and areas of improvement
        val strengths = mutableListOf<String>()
        val areasToImprove = mutableListOf<String>()

        if (wpm in 120..160) {
            strengths.add("Excellent pacing ($wpm WPM) maintaining steady conversational cadence.")
        } else if (wpm < 120) {
            areasToImprove.add("Pacing was slightly slow ($wpm WPM). Aim to project more momentum.")
        } else {
            areasToImprove.add("Pacing was brisk ($wpm WPM). Intersperse deliberate 1-second pauses for emphasis.")
        }

        if (fillerCount == 0) {
            strengths.add("Remarkable verbal clarity: Zero filler words detected!")
        } else if (fillerCount <= 2) {
            strengths.add("Controlled delivery: Only $fillerCount filler word(s) identified.")
        } else {
            val topFiller = detectedFillers.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "um"
            areasToImprove.add("High filler word frequency ($fillerCount total). Specifically replace '$topFiller' with quiet pauses.")
        }

        if (advancedWords >= 3) {
            strengths.add("Used strong professional vocabulary terms with contextual clarity.")
        } else {
            areasToImprove.add("Try substituting basic phrases with precise action verbs (e.g. 'leverage', 'substantiate').")
        }

        val feedbackSummary = if (overallScore >= 80) {
            "Confident and structured delivery. Your ideas flowed logically with strong executive presence."
        } else if (overallScore >= 65) {
            "Solid attempt with good foundational points. Focus on reducing hesitations and refining vocal cadence."
        } else {
            "Good start. Review the ideal response framework and practice pausing rather than using vocalized fillers."
        }

        return ExerciseResult(
            exerciseId = exerciseId,
            score = overallScore,
            userResponse = transcript,
            transcript = transcript,
            wpm = wpm,
            fillerCount = fillerCount,
            fillerWordsDetected = detectedFillers,
            feedback = feedbackSummary,
            strengths = strengths,
            areasToImprove = areasToImprove,
            xpEarned = baseXp + if (overallScore >= 80) 15 else 5
        )
    }
}
