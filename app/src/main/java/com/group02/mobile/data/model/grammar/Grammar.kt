package com.group02.mobile.data.model.grammar

data class Grammar(
    val title: String,
    val shortExplanation: String,
    val longExplanation: String,
    val formation: String,
    val examples: List<GrammarExample>,
    val pTag: String,
    val sTag: String
)

data class GrammarExample(
    val jp: String,
    val romaji: String,
    val en: String,
    val grammarAudio: String?
)
