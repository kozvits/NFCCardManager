package com.nfccardmanager.presentation.ui.theme

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

val Cyan400      = Color(0xFF26C6DA)
val Cyan600      = Color(0xFF00ACC1)
val Cyan800      = Color(0xFF00838F)
val CyanGlow     = Color(0xFF80DEEA)
val TealAccent   = Color(0xFF1DE9B6)
val ErrorRed     = Color(0xFFEF5350)

val NavyDark     = Color(0xFF0D1117)
val NavyMid      = Color(0xFF161B22)
val NavySurface  = Color(0xFF1C2333)
val NavyCard     = Color(0xFF21262D)
val NavyBorder   = Color(0xFF30363D)

val TextPrimary  = Color(0xFFE6EDF3)
val TextSecond   = Color(0xFF8B949E)

private val DarkColorScheme = darkColorScheme(
    primary            = Cyan400,
    onPrimary          = NavyDark,
    primaryContainer   = Color(0xFF003F47),
    onPrimaryContainer = CyanGlow,
    secondary          = TealAccent,
    onSecondary        = NavyDark,
    secondaryContainer = Color(0xFF003829),
    onSecondaryContainer = Color(0xFFB9F5E4),
    tertiary           = CyanGlow,
    onTertiary         = NavyDark,
    background         = NavyDark,
    onBackground       = TextPrimary,
    surface            = NavyMid,
    onSurface          = TextPrimary,
    surfaceVariant     = NavyCard,
    onSurfaceVariant   = TextSecond,
    outline            = NavyBorder,
    outlineVariant     = NavyBorder,
    error              = ErrorRed,
    onError            = Color.White,
    errorContainer     = Color(0xFF3B1010),
    onErrorContainer   = Color(0xFFFF8A80),
    inverseSurface     = TextPrimary,
    inverseOnSurface   = NavyDark,
    inversePrimary     = Cyan800,
    scrim              = Color(0x80000000)
)

private val LightColorScheme = lightColorScheme(
    primary            = Cyan800,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFB2EBF2),
    onPrimaryContainer = Color(0xFF00363D),
    secondary          = Color(0xFF00695C),
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00332D),
    background         = Color(0xFFF0F9FB),
    onBackground       = Color(0xFF0D1117),
    surface            = Color.White,
    onSurface          = Color(0xFF0D1117),
    surfaceVariant     = Color(0xFFE0F7FA),
    onSurfaceVariant   = Color(0xFF37474F),
    outline            = Color(0xFFB0BEC5),
    error              = ErrorRed,
    onError            = Color.White
)

@Composable
fun NFCCardManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            try {
                val context = view.context
                if (context is Activity) {
                    val window = context.window
                    window.statusBarColor = Color.Transparent.toArgb()
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars = !darkTheme
                }
            } catch (_: Exception) {}
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = NfcTypography,
        content     = content
    )
}
