package com.group02.mobile.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group02.mobile.ui.screens.*
import com.group02.mobile.viewmodel.AuthViewModel

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AuthScreen.Splash.route
    ) {
        // ── Splash ──────────────────────────────────────────────
        composable(
            route = AuthScreen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            SplashScreen(
                isLoggedIn = authViewModel.uiState.value.isLoggedIn,
                onNavigateToLogin = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(AuthScreen.Home.route) {
                        popUpTo(AuthScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ───────────────────────────────────────────────
        composable(
            route = AuthScreen.Login.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            }
        ) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(AuthScreen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AuthScreen.ForgotPassword.route)
                },
                onNavigateToHome = {
                    navController.navigate(AuthScreen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ────────────────────────────────────────────
        composable(
            route = AuthScreen.Register.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            }
        ) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(AuthScreen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Forgot Password ─────────────────────────────────────
        composable(
            route = AuthScreen.ForgotPassword.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ── Home (placeholder) ──────────────────────────────────
        composable(
            route = AuthScreen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            HomeScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
