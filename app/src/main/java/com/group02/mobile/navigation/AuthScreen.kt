package com.group02.mobile.navigation

sealed class AuthScreen(val route: String) {
    object Splash : AuthScreen("splash")
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
    object ForgotPassword : AuthScreen("forgot_password")
    object Home : AuthScreen("home")
    object SetupProfile : AuthScreen("setup_profile")
    object Profile : AuthScreen("profile")
    object ChangePassword : AuthScreen("change_password")
    object KanjiList : AuthScreen("kanji_list")
    object KanjiDetail : AuthScreen("kanji_detail/{kanji}") {
        fun createRoute(kanji: String) = "kanji_detail/$kanji"
    }
    object Dictionary : AuthScreen("dictionary")
    object Review : AuthScreen("review")
    object DailyLesson : AuthScreen("daily_lesson")
    object GrammarList : AuthScreen("grammar_list")
    object GrammarLevel : AuthScreen("grammar_level/{level}") {
        fun createRoute(level: String) = "grammar_level/$level"
    }
    object GrammarDetail : AuthScreen("grammar_detail/{title}") {
        fun createRoute(title: String) = "grammar_detail/${java.net.URLEncoder.encode(title, "UTF-8")}"
    }
}
