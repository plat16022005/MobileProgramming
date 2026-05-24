package com.group02.mobile.navigation

sealed class AuthScreen(val route: String) {
    object Splash : AuthScreen("splash")
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
    object ForgotPassword : AuthScreen("forgot_password")
    object Home : AuthScreen("home")
    object SetupProfile : AuthScreen("setup_profile")
    object Profile : AuthScreen("profile")
}
