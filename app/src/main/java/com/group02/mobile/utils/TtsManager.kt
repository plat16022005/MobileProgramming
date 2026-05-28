package com.group02.mobile.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.Locale

object TtsManager {
    private var tts: TextToSpeech? = null
    private var ready = false
    private var isJapaneseSupported = false

    fun init(context: Context) {
        if (tts != null) return
        
        // Cố gắng khởi tạo với Google TTS Engine trước
        tts = TextToSpeech(context, { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale.JAPANESE
                val result = tts?.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Japanese not available – falling back to English")
                    tts?.setLanguage(Locale.ENGLISH)
                    isJapaneseSupported = false
                } else {
                    Log.d("TTS", "TTS ready with Japanese")
                    isJapaneseSupported = true
                }
                ready = true
            } else {
                Log.e("TTS", "TTS init failed: $status")
            }
        }, "com.google.android.tts")
    }

    fun speak(context: Context, japanese: String, romaji: String) {
        Log.d("TTS", "speak() called | ready=$ready | isJp=$isJapaneseSupported")
        val engine = tts
        if (engine == null || !ready) {
            Toast.makeText(
                context, "Đang tải giọng đọc, thử lại sau giây lát…",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        engine.stop()
        
        if (isJapaneseSupported) {
            // Đọc chuẩn tiếng Nhật
            engine.speak(japanese, TextToSpeech.QUEUE_FLUSH, null, "utt_${System.currentTimeMillis()}")
        } else {
            // Máy không hỗ trợ tiếng Nhật -> Đọc Romaji bằng tiếng Anh
            engine.speak(romaji.ifEmpty { japanese }, TextToSpeech.QUEUE_FLUSH, null, "utt_${System.currentTimeMillis()}")
            Toast.makeText(
                context, "Chưa có dữ liệu tiếng Nhật, đang phát âm tạm Romaji.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}
