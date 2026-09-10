package com.procalc.pro.ui.theme

import androidx.compose.ui.graphics.Color
import com.procalc.pro.magic.Palette

/**
 * The full visual language of one look. Every colour the calculator draws comes
 * from here, so a new theme is a single data class away.
 */
data class CalcPalette(
    val backdropCore: Color,
    val backdropEdge: Color,

    val displayPrimary: Color,
    val displaySecondary: Color,

    val numberKeyTop: Color,
    val numberKeyBottom: Color,
    val numberText: Color,

    val functionKeyTop: Color,
    val functionKeyBottom: Color,
    val functionText: Color,

    val operatorKeyTop: Color,
    val operatorKeyBottom: Color,
    val operatorText: Color,

    val equalsTop: Color,
    val equalsBottom: Color,
    val equalsText: Color,

    val hairline: Color,
    val accent: Color,
    val surface: Color,
    val surfaceElevated: Color,
)

private val ObsidianGold = CalcPalette(
    backdropCore = Color(0xFF15151D),
    backdropEdge = Color(0xFF05050A),

    displayPrimary = Color(0xFFFAF7F0),
    displaySecondary = Color(0xFF8A8272),

    numberKeyTop = Color(0xFF1C1D24),
    numberKeyBottom = Color(0xFF111217),
    numberText = Color(0xFFF5F2EA),

    functionKeyTop = Color(0xFF262832),
    functionKeyBottom = Color(0xFF181A22),
    functionText = Color(0xFFD3C9B4),

    operatorKeyTop = Color(0xFF2E2718),
    operatorKeyBottom = Color(0xFF1D1810),
    operatorText = Color(0xFFEBC97C),

    equalsTop = Color(0xFFF2D78F),
    equalsBottom = Color(0xFFC9A227),
    equalsText = Color(0xFF14100A),

    hairline = Color(0x1FFFFFFF),
    accent = Color(0xFFD4AF37),
    surface = Color(0xFF12131A),
    surfaceElevated = Color(0xFF1B1D26),
)

private val MidnightPlatinum = CalcPalette(
    backdropCore = Color(0xFF101520),
    backdropEdge = Color(0xFF04060A),

    displayPrimary = Color(0xFFF4F7FB),
    displaySecondary = Color(0xFF7B8798),

    numberKeyTop = Color(0xFF1A2029),
    numberKeyBottom = Color(0xFF10151C),
    numberText = Color(0xFFF2F5F9),

    functionKeyTop = Color(0xFF242C38),
    functionKeyBottom = Color(0xFF161C25),
    functionText = Color(0xFFBAC6D6),

    operatorKeyTop = Color(0xFF1F2A3A),
    operatorKeyBottom = Color(0xFF141B26),
    operatorText = Color(0xFFCBDBF0),

    equalsTop = Color(0xFFE9EFF8),
    equalsBottom = Color(0xFFA9B9CF),
    equalsText = Color(0xFF0A0F16),

    hairline = Color(0x1FFFFFFF),
    accent = Color(0xFFC8D6E8),
    surface = Color(0xFF121820),
    surfaceElevated = Color(0xFF1B222C),
)

private val StealthGraphite = CalcPalette(
    backdropCore = Color(0xFF121316),
    backdropEdge = Color(0xFF0B0C0E),

    displayPrimary = Color(0xFFFFFFFF),
    displaySecondary = Color(0xFF9AA0A6),

    numberKeyTop = Color(0xFF22242A),
    numberKeyBottom = Color(0xFF1B1D22),
    numberText = Color(0xFFFFFFFF),

    functionKeyTop = Color(0xFF2E3138),
    functionKeyBottom = Color(0xFF262930),
    functionText = Color(0xFFE6E8EB),

    operatorKeyTop = Color(0xFF2E3138),
    operatorKeyBottom = Color(0xFF262930),
    operatorText = Color(0xFF8AB4F8),

    equalsTop = Color(0xFF2F7FEA),
    equalsBottom = Color(0xFF1A64D0),
    equalsText = Color(0xFFFFFFFF),

    hairline = Color(0x14FFFFFF),
    accent = Color(0xFF8AB4F8),
    surface = Color(0xFF16181C),
    surfaceElevated = Color(0xFF20232A),
)

fun paletteFor(palette: Palette): CalcPalette = when (palette) {
    Palette.OBSIDIAN_GOLD -> ObsidianGold
    Palette.MIDNIGHT_PLATINUM -> MidnightPlatinum
    Palette.STEALTH_GRAPHITE -> StealthGraphite
}
