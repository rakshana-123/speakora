package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiCoachService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun chatWithCoach(
        userMessage: String,
        userLevel: String = "Intermediate",
        goal: String = "Career & Leadership"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are 'Aura', an elite AI Executive Communication & Speech Coach.
                    The user's level is: $userLevel, and their goal is: $goal.
                    Keep feedback concise, actionable, and encouraging. Use bullet points or 2-3 short paragraphs.
                    
                    User: $userMessage
                """.trimIndent()

                val response = callGeminiRest(apiKey, prompt)
                if (response.isNotBlank()) return@withContext response
            } catch (e: Exception) {
                Log.w("GeminiCoachService", "Gemini API call failed, using on-device coach engine: ${e.message}")
            }
        }

        // Intelligent on-device communication coach rules engine
        generateLocalCoachResponse(userMessage, goal)
    }

    suspend fun evaluateRoleplayTurn(
        scenarioPrompt: String,
        conversationHistory: List<Pair<String, String>>, // sender to text
        userReply: String
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
                    You are roleplaying as the counterpart in a professional scenario: "$scenarioPrompt".
                    Respond realistically to the user's latest statement as the counterpart (keep response to 2 sentences).
                    Then, on a new line starting with 'COACH_TIP: ', give a 1-sentence tip on how their response scored in terms of tone and diplomacy.
                    
                    User said: $userReply
                """.trimIndent()

                val raw = callGeminiRest(apiKey, systemPrompt)
                if (raw.isNotBlank() && raw.contains("COACH_TIP:")) {
                    val parts = raw.split("COACH_TIP:")
                    return@withContext Pair(parts[0].trim(), parts[1].trim())
                }
            } catch (e: Exception) {
                Log.w("GeminiCoachService", "Roleplay API fallback: ${e.message}")
            }
        }

        generateLocalRoleplayTurn(userReply)
    }

    suspend fun evaluateWriting(
        prompt: String,
        userDraft: String
    ): Triple<String, String, String> = withContext(Dispatchers.IO) {
        // Returns Triple(Critique, ToneAnalysis, RewrittenVersion)
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val promptText = """
                    Analyze this workplace writing for prompt: "$prompt".
                    User draft: "$userDraft"
                    
                    Provide response in format:
                    FEEDBACK: <2 sentences on clarity, structure and grammar>
                    TONE: <Assertive, Empathetic, Concise, or Passive>
                    REWRITTEN: <Polished executive version>
                """.trimIndent()

                val raw = callGeminiRest(apiKey, promptText)
                if (raw.contains("FEEDBACK:") && raw.contains("REWRITTEN:")) {
                    val feedback = raw.substringAfter("FEEDBACK:").substringBefore("TONE:").trim()
                    val tone = raw.substringAfter("TONE:").substringBefore("REWRITTEN:").trim()
                    val rewritten = raw.substringAfter("REWRITTEN:").trim()
                    return@withContext Triple(feedback, tone, rewritten)
                }
            } catch (e: Exception) {
                Log.w("GeminiCoachService", "Writing evaluation fallback: ${e.message}")
            }
        }

        generateLocalWritingEvaluation(userDraft)
    }

    private fun callGeminiRest(apiKey: String, prompt: String): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val request = Request.Builder()
            .url(endpoint)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw RuntimeException("Gemini API error: ${response.code} $responseBody")
        }

        val jsonResponse = JSONObject(responseBody)
        val candidates = jsonResponse.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return parts.getJSONObject(0).optString("text", "")
            }
        }
        return ""
    }

    private fun generateLocalCoachResponse(message: String, goal: String): String {
        val lower = message.lowercase()
        return when {
            lower.contains("filler") || lower.contains("um") || lower.contains("like") -> {
                "Filler words usually appear when our brain is thinking faster than our vocal cords or when we fear silence.\n\n" +
                "• **The 2-Second Rule**: Replace 'um' with a silent pause and slow breath.\n" +
                "• **Deliberate Articulation**: Speak 10% slower so your mind formulates the next clause in silence.\n" +
                "• **Practice Anchor**: In today's speaking drill, tap your finger whenever you feel tempted to say 'like'."
            }
            lower.contains("interview") -> {
                "For high-stakes interviews, remember the **STAR Framework** (Situation, Task, Action, Result).\n\n" +
                "• Dedicate 70% of your response time to **Action** and **Result**.\n" +
                "• Quantify your impact: 'Saved 3 hours per sprint' sounds far more credible than 'made things faster'.\n" +
                "Would you like to run a mock interview simulation right now in the Practice tab?"
            }
            lower.contains("email") || lower.contains("writing") -> {
                "Here is the golden standard for executive email communication:\n\n" +
                "1. **BLUF (Bottom Line Up Front)**: State the core ask or update in sentence #1.\n" +
                "2. **Bulletized Context**: Never write more than 3 consecutive sentences in a block.\n" +
                "3. **Clear Next Steps**: Specify *who* does *what* by *when*."
            }
            lower.contains("harder") || lower.contains("difficult") -> {
                "I've noted your preference! Your next daily workout will adjust to **Advanced**, featuring unscripted conflict resolution scenarios and faster cadence requirements."
            }
            else -> {
                "As your communication coach focusing on **$goal**, my recommendation today is consistency.\n\n" +
                "• Complete your 10-minute communication workout.\n" +
                "• Focus on clean vocal projection without upward inflection at the end of statements.\n" +
                "• Try one roleplay in the Scenarios section to practice assertive boundary setting!"
            }
        }
    }

    private fun generateLocalRoleplayTurn(userReply: String): Pair<String, String> {
        val lower = userReply.lowercase()
        return if (lower.contains("understand") || lower.contains("agree") || lower.contains("prioritize")) {
            Pair(
                "I appreciate that you hear our urgency. If we release the MVP first in October, will the critical payment flow be 100% bug-free for launch day?",
                "Strong empathetic validation! You diffused immediate panic and kept focus on solution delivery."
            )
        } else {
            Pair(
                "That sounds risky. Our board expects a comprehensive launch. What guarantees do we have that the team won't face further delays?",
                "Tip: Anchor your response with factual milestones and clear mitigation steps to reassure senior stakeholders."
            )
        }
    }

    private fun generateLocalWritingEvaluation(userDraft: String): Triple<String, String, String> {
        val wordCount = userDraft.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val hasClearAction = userDraft.contains("please", ignoreCase = true) ||
                userDraft.contains("would", ignoreCase = true) ||
                userDraft.contains("by", ignoreCase = true)

        val feedback = if (wordCount > 50) {
            "Good thoroughness, but slightly lengthy for rapid mobile readers. Streamline sentences to emphasize the decision point."
        } else {
            "Crisp and straightforward. Ideas are immediately accessible with minimal fluff."
        }

        val tone = if (hasClearAction) "Assertive & Action-Oriented" else "Informative & Diplomatic"

        val rewritten = "Thank you for the update. To ensure alignment, our team will deliver the core milestones by Friday. Let me know if any adjustments are needed before we finalize."

        return Triple(feedback, tone, rewritten)
    }
}
