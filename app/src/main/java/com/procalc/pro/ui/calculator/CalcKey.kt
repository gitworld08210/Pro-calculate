package com.procalc.pro.ui.calculator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.procalc.pro.ui.theme.LocalCalcPalette

enum class KeyStyle { NUMBER, FUNCTION, FUNCTION_ACCENT, OPERATOR, EQUALS, MEMORY }

/**
 * One key on the pad: a soft-gradient pill with a hairline edge, a spring press
 * animation and an optional long-press action.
 */
@Composable
fun CalcKey(
    style: KeyStyle,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    fontSize: Int = 30,
    fontWeight: FontWeight = FontWeight.Light,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    val palette = LocalCalcPalette.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "keyScale",
    )

    val (top, bottom, contentColor) = when (style) {
        KeyStyle.NUMBER -> Triple(palette.numberKeyTop, palette.numberKeyBottom, palette.numberText)
        KeyStyle.FUNCTION -> Triple(palette.functionKeyTop, palette.functionKeyBottom, palette.functionText)
        KeyStyle.FUNCTION_ACCENT -> Triple(palette.functionKeyTop, palette.functionKeyBottom, palette.accent)
        KeyStyle.OPERATOR -> Triple(palette.operatorKeyTop, palette.operatorKeyBottom, palette.operatorText)
        KeyStyle.EQUALS -> Triple(palette.equalsTop, palette.equalsBottom, palette.equalsText)
        KeyStyle.MEMORY -> Triple(palette.functionKeyTop, palette.functionKeyBottom, palette.displaySecondary)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(percent = 50))
            .background(Brush.verticalGradient(listOf(top, bottom)))
            .border(
                width = 0.7.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        palette.hairline,
                        palette.hairline.copy(alpha = palette.hairline.alpha * 0.25f),
                    )
                ),
                shape = RoundedCornerShape(percent = 50),
            )
            .then(
                if (onLongClick != null) {
                    Modifier.combinedPress(interaction, onClick, onLongClick)
                } else {
                    Modifier.simplePress(interaction, onClick)
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        // A whisper of light along the top edge to give the key some depth.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(percent = 50))
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = if (style == KeyStyle.EQUALS) 0.10f else 0.045f),
                        0.45f to Color.Transparent,
                    )
                )
                .alpha(if (pressed) 0.4f else 1f)
        )

        when {
            icon != null -> Icon(
                imageVector = icon,
                contentDescription = contentDescription ?: label,
                tint = contentColor,
                modifier = Modifier.scale(1f),
            )
            label != null -> Text(
                text = label,
                color = contentColor,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
            )
        }
    }
}
