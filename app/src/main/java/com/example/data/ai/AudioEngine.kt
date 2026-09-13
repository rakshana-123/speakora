package com.example.data.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class AudioEngine(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("AudioEngine", "Failed to initialize TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true
                tts?.setSpeechRate(0.95f)
                tts?.setPitch(1.0f)
            }
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (isInitialized && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId_${System.currentTimeMillis()}")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error shutting down TTS: ${e.message}")
        }
    }
}
