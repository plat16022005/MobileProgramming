package com.group02.mobile.utils

object RomajiUtils {
    private val mapping = mapOf(
        "ka" to "か", "ki" to "き", "ku" to "く", "ke" to "け", "ko" to "こ",
        "sa" to "さ", "shi" to "し", "su" to "す", "se" to "せ", "so" to "そ",
        "ta" to "た", "chi" to "ち", "tsu" to "つ", "te" to "て", "to" to "と",
        "na" to "な", "ni" to "に", "nu" to "ぬ", "ne" to "ね", "no" to "の",
        "ha" to "は", "hi" to "ひ", "fu" to "ふ", "he" to "へ", "ho" to "ほ",
        "ma" to "ま", "mi" to "み", "mu" to "む", "me" to "め", "mo" to "も",
        "ya" to "や", "yu" to "ゆ", "yo" to "よ",
        "ra" to "ら", "ri" to "り", "ru" to "る", "re" to "れ", "ro" to "ろ",
        "wa" to "わ", "wo" to "を", "nn" to "ん",
        "ga" to "が", "gi" to "ぎ", "gu" to "ぐ", "ge" to "げ", "go" to "ご",
        "za" to "ざ", "ji" to "じ", "zu" to "ず", "ze" to "ぜ", "zo" to "ぞ",
        "da" to "だ", "di" to "ぢ", "du" to "づ", "de" to "で", "do" to "ど",
        "ba" to "ば", "bi" to "び", "bu" to "ぶ", "be" to "べ", "bo" to "ぼ",
        "pa" to "ぱ", "pi" to "ぴ", "pu" to "ぷ", "pe" to "ぺ", "po" to "ぽ",
        "a" to "あ", "i" to "い", "u" to "う", "e" to "え", "o" to "お"
    )

    /**
     * Converts a Romaji string to Hiragana. 
     * This is a simplified version for search purposes.
     */
    fun toHiragana(romaji: String): String {
        var input = romaji.lowercase().replace("r", "r") // ensure lowercase
        val result = StringBuilder()
        var i = 0
        while (i < input.length) {
            var found = false
            // Try matching 3 chars (e.g., kya, shu)
            if (i + 2 < input.length) {
                // Simplified: search doesn't strictly need youon for basic matching
            }
            
            // Try matching 2 chars
            if (!found && i + 1 < input.length) {
                val pair = input.substring(i, i + 2)
                if (mapping.containsKey(pair)) {
                    result.append(mapping[pair])
                    i += 2
                    found = true
                }
            }
            
            // Try matching 1 char
            if (!found) {
                val single = input.substring(i, i + 1)
                if (mapping.containsKey(single)) {
                    result.append(mapping[single])
                    i += 1
                    found = true
                } else {
                    result.append(single)
                    i += 1
                }
            }
        }
        return result.toString()
    }
}
