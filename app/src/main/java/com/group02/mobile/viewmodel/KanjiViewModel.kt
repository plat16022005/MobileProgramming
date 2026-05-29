package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import com.group02.mobile.data.model.alphabet.KanjiDetail

class KanjiViewModel : ViewModel() {
    private val _kanjiList = MutableStateFlow<List<String>>(emptyList())
    val kanjiList: StateFlow<List<String>> = _kanjiList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _kanjiDetail = MutableStateFlow<KanjiDetail?>(null)
    val kanjiDetail: StateFlow<KanjiDetail?> = _kanjiDetail

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading

    private val _detailError = MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError

    fun fetchAllKanji() {
        if (_kanjiList.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val list = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://kanjiapi.dev/v1/kanji/all")
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        val jsonArray = JSONArray(responseBody)
                        val result = mutableListOf<String>()
                        for (i in 0 until jsonArray.length()) {
                            result.add(jsonArray.getString(i))
                        }
                        result
                    } else {
                        throw Exception("Failed to fetch data")
                    }
                }
                _kanjiList.value = list
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchKanjiDetail(kanji: String) {
        viewModelScope.launch {
            _isDetailLoading.value = true
            _detailError.value = null
            _kanjiDetail.value = null
            try {
                val detail = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://kanjiapi.dev/v1/kanji/$kanji")
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        
                        val meanings = mutableListOf<String>()
                        val meaningsArray = jsonObject.optJSONArray("meanings")
                        if (meaningsArray != null) {
                            for (i in 0 until meaningsArray.length()) meanings.add(meaningsArray.getString(i))
                        }
                        
                        val kunReadings = mutableListOf<String>()
                        val kunArray = jsonObject.optJSONArray("kun_readings")
                        if (kunArray != null) {
                            for (i in 0 until kunArray.length()) kunReadings.add(kunArray.getString(i))
                        }
                        
                        val onReadings = mutableListOf<String>()
                        val onArray = jsonObject.optJSONArray("on_readings")
                        if (onArray != null) {
                            for (i in 0 until onArray.length()) onReadings.add(onArray.getString(i))
                        }

                        KanjiDetail(
                            kanji = jsonObject.getString("kanji"),
                            grade = if (jsonObject.isNull("grade")) null else jsonObject.getInt("grade"),
                            strokeCount = if (jsonObject.isNull("stroke_count")) null else jsonObject.getInt("stroke_count"),
                            meanings = translateToVietnamese(meanings),
                            kunReadings = kunReadings,
                            onReadings = onReadings,
                            jlpt = if (jsonObject.isNull("jlpt")) null else jsonObject.getInt("jlpt"),
                            unicode = jsonObject.optString("unicode", null),
                            heisigEn = jsonObject.optString("heisig_en", null)
                        )
                    } else {
                        throw Exception("Failed to fetch data")
                    }
                }
                _kanjiDetail.value = detail
            } catch (e: Exception) {
                _detailError.value = e.message ?: "An error occurred"
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    private fun translateToVietnamese(texts: List<String>): List<String> {
        if (texts.isEmpty()) return emptyList()
        val client = OkHttpClient()
        val translatedList = mutableListOf<String>()
        
        for (text in texts) {
            try {
                val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=vi&dt=t&q=${java.net.URLEncoder.encode(text, "UTF-8")}"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonArray = JSONArray(responseBody)
                    val segments = jsonArray.getJSONArray(0)
                    val sb = java.lang.StringBuilder()
                    for (i in 0 until segments.length()) {
                        sb.append(segments.getJSONArray(i).getString(0))
                    }
                    translatedList.add(sb.toString().lowercase())
                } else {
                    translatedList.add(text)
                }
            } catch (e: Exception) {
                translatedList.add(text)
            }
        }
        return translatedList
    }
}
