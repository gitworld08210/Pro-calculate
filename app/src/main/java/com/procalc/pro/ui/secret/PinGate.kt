package com.procalc.pro.ui.secret

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.procalc.pro.ui.clickableNoRipple
import com.procalc.pro.ui.theme.LocalCalcPalette

/**
 * Gate in front of the performer settings. Anyone who stumbles onto the hidden
 * gesture still sees nothing but a code prompt.
 */
@Composable
fun PinGate(
    onVerify: (String) -> Boolean,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = LocalCalcPalette.current
    var entered by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(entered) {
        if (entered.length == 4) {
            if (onVerify(entered)) {
                onSuccess()
            } else {
                error = true
                entered = ""
            }
        }
    }

    val shake by animateFloatAsState(
        targetValue = if (error) 1f else 0f,
        label = "pinError",
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(palette.surfaceElevated, palette.surface)))
                .border(0.7.dp, palette.hairline, RoundedCornerShape(28.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "ENTER CODE",
                color = palette.accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(4) { index ->
                    val filled = index < entered.length
                    Box(
                        modifier = Modifier
                            .size(if (filled) 13.dp else 11.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) palette.accent
                                else palette.displaySecondary.copy(alpha = 0.30f + 0.2f * shake)
                            )
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (error) "Incorrect code" else " ",
                color = palette.displaySecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(Modifier.height(10.dp))

            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "<"),
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEach { key ->
                        Box(modifier = Modifier.weight(1f)) {
                            when (key) {
                                "" -> Spacer(Modifier.fillMaxWidth().aspectRatio(1.4f))
                                "<" -> PinKey(
                                    onClick = { if (entered.isNotEmpty()) entered = entered.dropLast(1) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Backspace,
                                        contentDescription = "Delete",
                                        tint = palette.functionText,
                                    )
                                }
                                else -> PinKey(
                                    onClick = {
                                        error = false
                                        if (entered.length < 4) entered += key
                                    },
                                ) {
                                    Text(
                                        text = key,
                                        color = palette.numberText,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Light,
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Text(
                text = "Cancel",
                color = palette.displaySecondary,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickableNoRipple(onDismiss)
                    .padding(8.dp),
            )
        }
    }
}

@Composable
private fun PinKey(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val palette = LocalCalcPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.4f)
            .clip(RoundedCornerShape(50))
            .background(Brush.verticalGradient(listOf(palette.numberKeyTop, palette.numberKeyBottom)))
            .border(0.7.dp, palette.hairline, RoundedCornerShape(50))
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}
