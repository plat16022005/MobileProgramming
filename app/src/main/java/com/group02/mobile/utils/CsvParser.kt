package com.group02.mobile.utils

import com.group02.mobile.data.model.vocabulary.UserVocabulary
import java.util.UUID

object CsvParser {
    /**
     * Parse CSV string into a list of UserVocabulary.
     * Expected CSV format: Kanji/Katakana, Hiragana, Romaji, Meaning, ... (other columns ignored)
     * Skip the header row.
     */
    fun parseCsv(csvContent: String): List<UserVocabulary> {
        val vocabularies = mutableListOf<UserVocabulary>()
        val lines = csvContent.lines()
        
        // Bỏ qua dòng header nếu có (thường là dòng 1)
        val dataLines = if (lines.isNotEmpty() && lines[0].contains("Kanji", ignoreCase = true)) {
            lines.drop(1)
        } else {
            lines
        }

        for (line in dataLines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            // Phân tách bởi dấu phẩy, xử lý dấu phẩy trong ngoặc kép nếu cần
            val columns = trimmedLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()).map { 
                it.replace("\"", "").trim() 
            }

            if (columns.size >= 4) {
                val kanji = columns[0]
                val hiragana = columns[1]
                val romaji = columns[2]
                val meaning = columns[3]

                // Validate
                if (hiragana.isNotEmpty() && meaning.isNotEmpty()) {
                    vocabularies.add(
                        UserVocabulary(
                            id = UUID.randomUUID().toString(),
                            kanji = kanji,
                            hiragana = hiragana,
                            romaji = romaji,
                            meaning = meaning
                        )
                    )
                }
            }
        }
        
        return vocabularies
    }
}
