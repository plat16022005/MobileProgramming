package com.group02.mobile.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NihonlishDarkColorScheme = darkColorScheme(
    primary = NihonRedLight,
    onPrimary = TextPrimary,
    primaryContainer = NihonRedDark,
    onPrimaryContainer = SakuraPinkLight,
    secondary = SakuraPinkDark,
    onSecondary = InkBlack,
    secondaryContainer = InkMedium,
    onSecondaryContainer = SakuraPink,
    tertiary = GoldAccent,
    onTertiary = InkBlack,
    background = InkBlack,
    onBackground = TextPrimary,
    surface = InkDark,
    onSurface = TextPrimary,
    surfaceVariant = InkMedium,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary,
    outline = CardBorder,
    outlineVariant = InkLight
)

@Composable
fun SakumiTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = NihonlishDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NihonlishTypography,
        content = content
    )
}