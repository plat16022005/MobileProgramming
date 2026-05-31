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
}
