package com.procalc.pro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.procalc.pro.magic.Palette

val LocalCalcPalette = staticCompositionLocalOf { paletteFor(Palette.OBSIDIAN_GOLD) }

private val LuxeTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 64.sp,
        letterSpacing = (-1.5).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.4.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.3.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 1.4.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.2.sp,
    ),
)

@Composable
fun ProCalculatorTheme(
    palette: Palette,
    content: @Composable () -> Unit,
) {
    val calcPalette = paletteFor(palette)

    val scheme = darkColorScheme(
        primary = calcPalette.accent,
        onPrimary = calcPalette.equalsText,
        background = calcPalette.backdropEdge,
        onBackground = calcPalette.displayPrimary,
        surface = calcPalette.surface,
        onSurface = calcPalette.displayPrimary,
        surfaceVariant = calcPalette.surfaceElevated,
        onSurfaceVariant = calcPalette.displaySecondary,
        outline = calcPalette.hairline,
    )

    androidx.compose.runtime.CompositionLocalProvider(LocalCalcPalette provides calcPalette) {
        MaterialTheme(
            colorScheme = scheme,
            typography = LuxeTypography,
            content = content,
        )
    }
}
