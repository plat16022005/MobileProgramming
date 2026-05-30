package com.group02.mobile.data.remote

import com.group02.mobile.viewmodel.DictionaryWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class JishoService {
    private val client = OkHttpClient()

    suspend fun searchWords(keyword: String): List<DictionaryWord> = withContext(Dispatchers.IO) {
        try {
            val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
            val url = "https://jisho.org/api/v1/search/words?keyword=$encodedKeyword"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val dataArray = jsonObject.getJSONArray("data")
                val words = mutableListOf<DictionaryWord>()

                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    val japaneseArray = item.optJSONArray("japanese")
                    if (japaneseArray == null || japaneseArray.length() == 0) continue
                    
                    val japanese = japaneseArray.getJSONObject(0)
                    val word = japanese.optString("word", "")
                    val reading = japanese.optString("reading", "")
                    
                    val senses = item.optJSONArray("senses")
                    val meaningsList = mutableListOf<String>()
                    if (senses != null) {
                        // Take up to 3 senses to avoid too much text
                        val numSenses = minOf(senses.length(), 3)
                        for (j in 0 until numSenses) {
                            val sense = senses.getJSONObject(j)
                            val definitions = sense.optJSONArray("english_definitions")
                            if (definitions != null) {
                                // Take up to 3 definitions per sense
                                val numDefs = minOf(definitions.length(), 3)
                                for (k in 0 until numDefs) {
                                    meaningsList.add(definitions.getString(k))
                                }
                            }
                        }
                    }

                    if (word.isEmpty() && reading.isEmpty()) continue

                    words.add(
                        DictionaryWord(
                            word = word.ifEmpty { reading },
                            hiragana = reading,
                            romaji = "",
                            meanings = meaningsList.distinct().joinToString(", ")
                        )
                    )
                }
                words
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
