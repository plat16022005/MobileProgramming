package com.group02.mobile.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

object TranslationUtils {
    private val client = OkHttpClient()

    private fun isProbablyVietnamese(text: String): Boolean {
        if (text.isEmpty()) return false
        // Vietnamese-specific characters (with accents and unique letters)
        val vietnameseChars = "àáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệđìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ"
        val count = text.count { vietnameseChars.contains(it, ignoreCase = true) }
        // If more than 10% of characters are Vietnamese-specific, or at least 1 if short
        return if (text.length < 5) count >= 1 else count.toFloat() / text.length > 0.1
    }

    suspend fun translateToEnglish(text: String): String = withContext(Dispatchers.IO) {
        if (text.isBlank() || !isProbablyVietnamese(text)) return@withContext text
        
        try {
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=vi&tl=en&dt=t&q=${URLEncoder.encode(text, "UTF-8")}"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val jsonArray = JSONArray(responseBody)
                val segments = jsonArray.getJSONArray(0)
                val sb = StringBuilder()
                for (i in 0 until segments.length()) {
                    sb.append(segments.getJSONArray(i).getString(0))
                }
                sb.toString().trim().lowercase()
            } else {
                text
            }
        } catch (e: Exception) {
            text
        }
    }

    suspend fun translateToVietnamese(text: String): String = withContext(Dispatchers.IO) {
        if (text.isBlank() || isProbablyVietnamese(text)) return@withContext text
        
        try {
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=vi&dt=t&q=${URLEncoder.encode(text, "UTF-8")}"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val jsonArray = JSONArray(responseBody)
                val segments = jsonArray.getJSONArray(0)
                val sb = StringBuilder()
                for (i in 0 until segments.length()) {
                    sb.append(segments.getJSONArray(i).getString(0))
                }
                sb.toString().trim().lowercase()
            } else {
                text
            }
        } catch (e: Exception) {
            text
        }
    }

    suspend fun translateListToVietnamese(texts: List<String>): List<String> = coroutineScope {
        texts.map { async { translateToVietnamese(it) } }.awaitAll()
    }
    
    /**
     * Translates a comma-separated string of meanings and joins them back.
     */
    suspend fun translateMeaningsString(meanings: String): String = coroutineScope {
        if (meanings.isBlank()) return@coroutineScope meanings
        
        val items = meanings.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (items.isEmpty()) return@coroutineScope meanings
        
        val translated = items.map { async { translateToVietnamese(it) } }.awaitAll()
        translated.joinToString(", ")
    }
}
