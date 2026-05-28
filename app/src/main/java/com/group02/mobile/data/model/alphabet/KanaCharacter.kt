package com.group02.mobile.data.model.alphabet

data class KanaCharacter(
    val hiragana: String,
    val katakana: String,
    val romaji: String,
    val exampleWord: String = "",
    val exampleWordRomaji: String = "",
    val exampleWordMeaning: String = ""
)
