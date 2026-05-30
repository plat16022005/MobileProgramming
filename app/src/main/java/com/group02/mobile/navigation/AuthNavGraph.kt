package com.group02.mobile.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.group02.mobile.viewmodel.UserVocabularyViewModel

import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.ui.screens.vocabulary.MyVocabularyScreen
import com.group02.mobile.ui.screens.vocabulary.AddEditVocabularyScreen
import com.group02.mobile.ui.screens.vocabulary.CustomPracticeSetupScreen
import com.group02.mobile.viewmodel.CustomPracticeViewModel
import androidx.compose.ui.platform.LocalContext

object VocabularyRoutes {
    const val MY_VOCABULARY = "my_vocabulary"
    const val ADD_EDIT_VOCABULARY = "add_edit_vocabulary?vocabularyId={vocabularyId}"
    const val CUSTOM_PRACTICE_SETUP = "custom_practice_setup"
    
    fun createAddEditRoute(id: String?) = if (id == null) "add_edit_vocabulary" else "add_edit_vocabulary?vocabularyId=$id"
}

object AlphabetRoutes {
    const val ALPHABET_HOME = "alphabet_home"
    const val KANA_ROW_LIST = "kana_row_list/{kanaType}"
    const val KANA_ROW_DETAIL = "kana_row_detail/{rowId}/{kanaType}"
    const val STUDY = "kana_study/{rowId}/{kanaType}"
    const val WRITING = "kana_writing/{rowId}/{kanaType}"
    const val FLASHCARD = "kana_flashcard/{rowId}/{kanaType}"
    const val QUIZ = "kana_quiz/{rowId}/{kanaType}"
    const val CHALLENGE = "kana_challenge/{rowId}/{kanaType}"
}

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val kanaViewModel: KanaViewModel = viewModel()
    val kanjiViewModel: KanjiViewModel = viewModel()
    val dictionaryViewModel: DictionaryViewModel = viewModel()
    val userVocabularyViewModel: UserVocabularyViewModel = viewModel()
    val customPracticeViewModel: CustomPracticeViewModel = viewModel()
    val context = LocalContext.current
    
    val authUiState by authViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        userVocabularyViewModel.initialize(context)
    }
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
                isLoggedIn = authUiState.isLoggedIn,
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

        // ── Home ──────────────────────────────────
        composable(
            route = AuthScreen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
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
                },
                onNavigateToCustomPractice = {
                    navController.navigate(VocabularyRoutes.CUSTOM_PRACTICE_SETUP)
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
                customPracticeViewModel = customPracticeViewModel,
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
                customPracticeViewModel = customPracticeViewModel,
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
                customPracticeViewModel = customPracticeViewModel,
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
                customPracticeViewModel = customPracticeViewModel,
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
                },
                onNavigateToMyVocabulary = {
                    navController.navigate(VocabularyRoutes.MY_VOCABULARY)
                }
            )
        }

        // ── My Vocabulary ──────────────────────────────────────────
        composable(
            route = VocabularyRoutes.MY_VOCABULARY,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) {
            MyVocabularyScreen(
                viewModel = userVocabularyViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddEdit = { id ->
                    navController.navigate(VocabularyRoutes.createAddEditRoute(id))
                }
            )
        }

        // ── Add/Edit Vocabulary ──────────────────────────────────
        composable(
            route = VocabularyRoutes.ADD_EDIT_VOCABULARY,
            arguments = listOf(navArgument("vocabularyId") { type = NavType.StringType; nullable = true; defaultValue = null }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) { backStackEntry ->
            val vocabularyId = backStackEntry.arguments?.getString("vocabularyId")
            AddEditVocabularyScreen(
                vocabularyId = vocabularyId,
                viewModel = userVocabularyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Custom Practice Setup ──────────────────────────────────
        composable(
            route = VocabularyRoutes.CUSTOM_PRACTICE_SETUP,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) {
            CustomPracticeSetupScreen(
                customViewModel = customPracticeViewModel,
                userVocabViewModel = userVocabularyViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPractice = { mode ->
                    val typeStr = KanaType.HIRAGANA.name
                    when (mode) {
                        com.group02.mobile.data.model.vocabulary.PracticeMode.STUDY -> navController.navigate("kana_study/custom/$typeStr")
                        com.group02.mobile.data.model.vocabulary.PracticeMode.FLASHCARD -> navController.navigate("kana_flashcard/custom/$typeStr")
                        com.group02.mobile.data.model.vocabulary.PracticeMode.QUIZ -> navController.navigate("kana_quiz/custom/$typeStr")
                        com.group02.mobile.data.model.vocabulary.PracticeMode.CHALLENGE -> navController.navigate("kana_challenge/custom/$typeStr")
                    }
                }
            )
        }
    }
}
