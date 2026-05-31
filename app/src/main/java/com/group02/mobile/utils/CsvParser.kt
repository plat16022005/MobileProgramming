package com.group02.mobile.utils

import android.util.Log
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import java.io.BufferedReader
import java.io.StringReader

object CsvParser {
    private const val TAG = "CsvParser"

    /**
     * Parses a CSV string and returns a list of UserVocabulary objects.
     * Expects columns in order: Kanji/Katakana, Hiragana, Romaji, Ý nghĩa (Tiếng Việt)
     */
    fun parse(csvContent: String): List<UserVocabulary> {
        val result = mutableListOf<UserVocabulary>()
        val reader = BufferedReader(StringReader(csvContent))
        
        var isFirstLine = true
        var lineNumber = 0
        
        reader.useLines { lines ->
            lines.forEach { line ->
                lineNumber++
                if (isFirstLine) {
                    isFirstLine = false
                    return@forEach // Skip header
                }
                
                if (line.isBlank()) return@forEach
                
                // Simple CSV splitting by comma. 
                // For a more robust solution, consider a CSV parsing library, but this should work for simple cases.
                // Assuming columns might be quoted. For a simple implementation, we'll split by comma.
                val tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                
                if (tokens.size >= 4) {
                    val kanji = tokens[0].trim().removeSurrounding("\"")
                    val hiragana = tokens[1].trim().removeSurrounding("\"")
                    val romaji = tokens[2].trim().removeSurrounding("\"")
                    val meaning = tokens[3].trim().removeSurrounding("\"")
                    
                    if (hiragana.isNotEmpty() && meaning.isNotEmpty()) {
                        result.add(
                            UserVocabulary(
                                kanji = kanji,
                                hiragana = hiragana,
                                romaji = romaji,
                                meaning = meaning
                            )
                        )
                    } else {
                        Log.w(TAG, "Line $lineNumber skipped: hiragana or meaning is empty. Line content: $line")
                    }
                } else {
                    Log.w(TAG, "Line $lineNumber skipped: Not enough columns. Line content: $line")
                }
            }
        }
        
        return result
    }
}
