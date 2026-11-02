package de.thoska.pulse.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ThoskaBoyColorScheme = darkColorScheme(
    primary = LcdGreenDeep,
    onPrimary = LcdGreenBg,
    primaryContainer = LcdGreenMedium,
    onPrimaryContainer = LcdGreenDeep,
    secondary = BezelTeal,
    onSecondary = LcdGreenBg,
    tertiary = ButtonRed,
    onTertiary = LcdGreenBg,
    background = ShellOrange,
    onBackground = CasingTextWhite,
    surface = BezelTeal,
    onSurface = LcdGreenBg
)

@Composable
fun ThoskaPulseTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = ShellOrangeDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = ThoskaBoyColorScheme,
        typography = Typography,
        content = content
    )
}
