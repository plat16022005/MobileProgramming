package com.group02.mobile.data.repository

import android.content.Context
import android.util.Log
import com.group02.mobile.data.model.grammar.Grammar
import com.group02.mobile.data.model.grammar.GrammarExample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.nio.charset.Charset

class GrammarRepository(private val context: Context) {
    suspend fun getGrammarList(level: String): List<Grammar> = withContext(Dispatchers.IO) {
        val fileName = "grammar_${level.lowercase()}.json"
        try {
            val inputStream = context.assets.open(fileName)
            // Detect encoding from BOM (UTF-16 LE: FF FE, UTF-16 BE: FE FF, UTF-8 BOM: EF BB BF)
            val firstBytes = ByteArray(4)
            val peeked = inputStream.read(firstBytes)
            inputStream.close()

            val charset: Charset = when {
                peeked >= 2 && firstBytes[0] == 0xFF.toByte() && firstBytes[1] == 0xFE.toByte() ->
                    Charsets.UTF_16LE   // UTF-16 LE with BOM
                peeked >= 2 && firstBytes[0] == 0xFE.toByte() && firstBytes[1] == 0xFF.toByte() ->
                    Charsets.UTF_16BE   // UTF-16 BE with BOM
                peeked >= 3 && firstBytes[0] == 0xEF.toByte() && firstBytes[1] == 0xBB.toByte() && firstBytes[2] == 0xBF.toByte() ->
                    Charsets.UTF_8      // UTF-8 with BOM (skip handled below)
                else ->
                    Charsets.UTF_8      // Plain UTF-8
            }

            // Re-open and read with detected charset, letting InputStreamReader handle the BOM
            val jsonString = context.assets.open(fileName).use { stream ->
                InputStreamReader(stream, charset).use { reader ->
                    reader.readText()
                }
            }.trimStart('\uFEFF') // strip BOM character if present in string

            val jsonArray = JSONArray(jsonString)
            val grammarList = mutableListOf<Grammar>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i)
                if (obj != null) {
                    grammarList.add(parseGrammar(obj))
                }
            }
            Log.d("GrammarRepository", "Loaded ${grammarList.size} items from $fileName (charset: ${charset.name()})")
            grammarList
        } catch (e: Exception) {
            Log.e("GrammarRepository", "Error loading grammar list for level $level", e)
            emptyList()
        }
    }

    private fun parseGrammar(obj: JSONObject): Grammar {
        val examplesArray = obj.optJSONArray("examples") ?: JSONArray()
        val examples = mutableListOf<GrammarExample>()
        for (i in 0 until examplesArray.length()) {
            val exObj = examplesArray.optJSONObject(i)
            if (exObj != null) {
                examples.add(
                    GrammarExample(
                        jp = exObj.optString("jp", ""),
                        romaji = exObj.optString("romaji", ""),
                        en = exObj.optString("en", ""),
                        grammarAudio = exObj.optString("grammar_audio", null)
                    )
                )
            }
        }

        return Grammar(
            title = obj.optString("title", ""),
            shortExplanation = obj.optString("short_explanation", ""),
            longExplanation = obj.optString("long_explanation", ""),
            formation = obj.optString("formation", ""),
            examples = examples,
            pTag = obj.optString("p_tag", ""),
            sTag = obj.optString("s_tag", "")
        )
    }
}
