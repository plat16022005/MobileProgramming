package com.group02.mobile.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.group02.mobile.ui.screens.*
import com.group02.mobile.ui.screens.alphabet.*
import com.group02.mobile.viewmodel.AuthViewModel
import com.group02.mobile.viewmodel.KanaViewModel
import com.group02.mobile.viewmodel.KanjiViewModel
import com.group02.mobile.viewmodel.DictionaryViewModel
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.viewmodel.NotificationViewModel

object AlphabetRoutes {
    const val ALPHABET_HOME = "alphabet_home"
    const val KANA_ROW_LIST = "kana_row_list/{kanaType}"
    const val KANA_ROW_DETAIL = "kana_row_detail/{rowId}/{kanaType}"
    const val STUDY = "kana_study/{rowId}/{kanaType}"
    const val WRITING = "kana_writing/{rowId}/{kanaType}"
    const val FLASHCARD = "kana_flashcard/{rowId}/{kanaType}"
    const val QUIZ = "kana_quiz/{rowId}/{kanaType}"
    const val CHALLENGE = "kana_challenge/{rowId}/{kanaType}"

    const val NOTIFICATION_SETTING = "notification_setting"
}

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val kanaViewModel: KanaViewModel = viewModel()
    val kanjiViewModel: KanjiViewModel = viewModel()
    val dictionaryViewModel: DictionaryViewModel = viewModel()
    val notificationViewModel : NotificationViewModel = viewModel()
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
        // Notification
        composable(
            route = AlphabetRoutes.NOTIFICATION_SETTING,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            NotificationSettingScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = notificationViewModel
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

        // ── Home ──────────────────────────────────
        composable(
            route = AuthScreen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                notificationViewModel.asyncStateSystem()
            }
            HomeScreen(
                viewModel = authViewModel,
                onNavigateToSetupProfile = {
                    navController.navigate(AuthScreen.SetupProfile.route) {
                        popUpTo(AuthScreen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(AuthScreen.Profile.route)
                },
                onNavigateToAlphabet = {
                    navController.navigate(AlphabetRoutes.ALPHABET_HOME)
                },
                onNavigateToKanjiList = {
                    navController.navigate(AuthScreen.KanjiList.route)
                },
                onNavigateToDictionary = {
                    navController.navigate(AuthScreen.Dictionary.route)
                },
                onNavigateToNotificationSetting = {
                    navController.navigate(AlphabetRoutes.NOTIFICATION_SETTING)
                },

                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Setup Profile ───────────────────────────────────────
        composable(
            route = AuthScreen.SetupProfile.route,
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
            val isEditMode = authViewModel.uiState.collectAsState().value.userAccount?.profileCompleted == true
            SetupProfileScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(AuthScreen.Home.route) {
                        popUpTo(AuthScreen.SetupProfile.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                isEditMode = isEditMode
            )
        }

        // ── Profile ─────────────────────────────────────────────
        composable(
            route = AuthScreen.Profile.route,
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
            }
        ) {
            ProfileScreen(
                viewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = {
                    navController.navigate(AuthScreen.SetupProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(AuthScreen.ChangePassword.route)
                }
            )
        }

        // ── Change Password ──────────────────────────────────────
        composable(
            route = AuthScreen.ChangePassword.route,
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
            }
        ) {
            ChangePasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        // ── Alphabet Module ──────────────────────────────────────
        composable(
            route = AlphabetRoutes.ALPHABET_HOME,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) {
            AlphabetHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onSelectKanaType = { type ->
                    val typeStr = type.name
                    navController.navigate("kana_row_list/$typeStr")
                }
            )
        }

        composable(
            route = AlphabetRoutes.KANA_ROW_LIST,
            arguments = listOf(navArgument("kanaType") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) { backStackEntry ->
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)
            
            KanaRowListScreen(
                kanaType = kanaType,
                onNavigateBack = { navController.popBackStack() },
                onRowSelected = { row ->
                    navController.navigate("kana_row_detail/${row.rowId}/$kanaTypeStr")
                }
            )
        }

        composable(
            route = AlphabetRoutes.KANA_ROW_DETAIL,
            arguments = listOf(
                navArgument("rowId") { type = NavType.StringType },
                navArgument("kanaType") { type = NavType.StringType }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) { backStackEntry ->
            val rowId = backStackEntry.arguments?.getString("rowId") ?: ""
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)

            KanaRowDetailScreen(
                rowId = rowId,
                kanaType = kanaType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStudy = { navController.navigate("kana_study/$rowId/$kanaTypeStr") },
                onNavigateToWriting = { navController.navigate("kana_writing/$rowId/$kanaTypeStr") },
                onNavigateToFlashCard = { navController.navigate("kana_flashcard/$rowId/$kanaTypeStr") },
                onNavigateToQuiz = { navController.navigate("kana_quiz/$rowId/$kanaTypeStr") },
                onNavigateToChallenge = { navController.navigate("kana_challenge/$rowId/$kanaTypeStr") }
            )
        }

        composable(
            route = AlphabetRoutes.STUDY,
            arguments = listOf(
                navArgument("rowId") { type = NavType.StringType },
                navArgument("kanaType") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) { backStackEntry ->
            val rowId = backStackEntry.arguments?.getString("rowId") ?: ""
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)

            StudyScreen(
                rowId = rowId,
                kanaType = kanaType,
                viewModel = kanaViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AlphabetRoutes.WRITING,
            arguments = listOf(
                navArgument("rowId") { type = NavType.StringType },
                navArgument("kanaType") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) { backStackEntry ->
            val rowId = backStackEntry.arguments?.getString("rowId") ?: ""
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)

            WritingPracticeScreen(
                rowId = rowId,
                kanaType = kanaType,
                viewModel = kanaViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AlphabetRoutes.FLASHCARD,
            arguments = listOf(
                navArgument("rowId") { type = NavType.StringType },
                navArgument("kanaType") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) { backStackEntry ->
            val rowId = backStackEntry.arguments?.getString("rowId") ?: ""
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)

            FlashCardScreen(
                rowId = rowId,
                kanaType = kanaType,
                viewModel = kanaViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AlphabetRoutes.QUIZ,
            arguments = listOf(
                navArgument("rowId") { type = NavType.StringType },
                navArgument("kanaType") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) { backStackEntry ->
            val rowId = backStackEntry.arguments?.getString("rowId") ?: ""
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)

            QuizScreen(
                rowId = rowId,
                kanaType = kanaType,
                viewModel = kanaViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AlphabetRoutes.CHALLENGE,
            arguments = listOf(
                navArgument("rowId") { type = NavType.StringType },
                navArgument("kanaType") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) { backStackEntry ->
            val rowId = backStackEntry.arguments?.getString("rowId") ?: ""
            val kanaTypeStr = backStackEntry.arguments?.getString("kanaType") ?: KanaType.HIRAGANA.name
            val kanaType = KanaType.valueOf(kanaTypeStr)

            ChallengeScreen(
                rowId = rowId,
                kanaType = kanaType,
                viewModel = kanaViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ── Kanji List ──────────────────────────────────────────
        composable(
            route = AuthScreen.KanjiList.route,
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
            }
        ) {
            KanjiListScreen(
                viewModel = kanjiViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onKanjiClick = { kanji ->
                    navController.navigate(AuthScreen.KanjiDetail.createRoute(kanji))
                }
            )
        }

        // ── Kanji Detail ──────────────────────────────────────────
        composable(
            route = AuthScreen.KanjiDetail.route,
            arguments = listOf(navArgument("kanji") { type = NavType.StringType }),
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
            }
        ) { backStackEntry ->
            val kanji = backStackEntry.arguments?.getString("kanji") ?: ""
            KanjiDetailScreen(
                kanji = kanji,
                viewModel = kanjiViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Dictionary ────────────────────────────────────────────
        composable(
            route = AuthScreen.Dictionary.route,
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
            }
        ) {
            DictionaryScreen(
                viewModel = dictionaryViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
