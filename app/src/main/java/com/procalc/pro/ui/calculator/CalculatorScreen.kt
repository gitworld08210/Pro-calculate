package com.procalc.pro.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.procalc.pro.engine.CalcState
import com.procalc.pro.engine.Ops
import com.procalc.pro.magic.ArmGesture
import com.procalc.pro.magic.MagicSettings
import com.procalc.pro.magic.Palette
import com.procalc.pro.ui.theme.LocalCalcPalette
import com.procalc.pro.vm.Key

@Composable
fun CalculatorScreen(
    state: CalcState,
    settings: MagicSettings,
    onKey: (Key) -> Unit,
    onQuickSet: () -> Unit,
    onArmToggle: () -> Unit,
    onPanic: () -> Unit,
    onOpenSecret: () -> Unit,
) {
    val palette = LocalCalcPalette.current
    val systemStyle = settings.palette == Palette.SYSTEM_RED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to palette.backdropCore,
                    0.5f to palette.backdropEdge,
                    1.0f to palette.backdropEdge,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            if (systemStyle) {
                SystemTopBar()
            }

            DisplayPanel(
                state = state,
                settings = settings,
                onArmToggle = onArmToggle,
                onOpenSecret = onOpenSecret,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (systemStyle) 0.24f else 0.30f),
            )

            if (systemStyle) {
                SystemToolIcons()
            }

            Keypad(
                systemStyle = systemStyle,
                onKey = onKey,
                onQuickSet = onQuickSet,
                onPanic = onPanic,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (systemStyle) 0.76f else 0.70f)
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 10.dp),
            )
        }
    }
}

/** The two dummy icons the stock calculator shows top-right (purely decorative). */
@Composable
private fun SystemTopBar() {
    val palette = LocalCalcPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Icon(
            imageVector = Icons.Outlined.Fullscreen,
            contentDescription = null,
            tint = palette.displayPrimary.copy(alpha = 0.85f),
            modifier = Modifier.size(26.dp),
        )
        Spacer(Modifier.size(22.dp))
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = null,
            tint = palette.displayPrimary.copy(alpha = 0.85f),
            modifier = Modifier.size(24.dp),
        )
    }
}

/** History / scientific / converter icons above the keypad (purely decorative). */
@Composable
private fun SystemToolIcons() {
    val palette = LocalCalcPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 26.dp, end = 26.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        listOf(
            Icons.Outlined.History,
            Icons.Outlined.Functions,
            Icons.Outlined.CurrencyExchange,
        ).forEach { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.displaySecondary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

// ------------------------------------------------------------------ display

@Composable
private fun DisplayPanel(
    state: CalcState,
    settings: MagicSettings,
    onArmToggle: () -> Unit,
    onOpenSecret: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalCalcPalette.current
    var size by remember { mutableStateOf(IntSize.Zero) }

    val expressionScroll = rememberScrollState()
    val valueScroll = rememberScrollState()

    LaunchedEffect(state.expression) { expressionScroll.scrollTo(expressionScroll.maxValue) }
    LaunchedEffect(state.display) { valueScroll.scrollTo(valueScroll.maxValue) }

    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(settings.armGesture, size) {
                detectTapGestures(
                    onLongPress = { offset ->
                        when {
                            inTopLeft(offset, size) -> onOpenSecret()
                            settings.armGesture == ArmGesture.LONG_PRESS_DISPLAY -> onArmToggle()
                        }
                    },
                    onDoubleTap = { offset ->
                        when (settings.armGesture) {
                            ArmGesture.DOUBLE_TAP_TOP_RIGHT ->
                                if (inTopRight(offset, size)) onArmToggle()
                            ArmGesture.DOUBLE_TAP_TOP_LEFT ->
                                if (inTopLeft(offset, size)) onArmToggle()
                            ArmGesture.LONG_PRESS_DISPLAY -> Unit
                        }
                    },
                )
            }
    ) {
        // Near-invisible confirmation that a routine is live. Unreadable at arm's length.
        if (settings.armedIndicator && settings.isLive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 10.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(palette.accent.copy(alpha = 0.30f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = state.expression,
                color = palette.displaySecondary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(expressionScroll),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.display,
                color = palette.displayPrimary,
                fontSize = displayFontSize(state.display).sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-1.5).sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(valueScroll),
            )
        }
    }
}

private fun inTopLeft(offset: Offset, size: IntSize): Boolean =
    size.width > 0 && offset.x < size.width * 0.35f && offset.y < size.height * 0.45f

private fun inTopRight(offset: Offset, size: IntSize): Boolean =
    size.width > 0 && offset.x > size.width * 0.65f && offset.y < size.height * 0.45f

/** Shrinks the primary line so long results and dates never clip. */
private fun displayFontSize(text: String): Int = when (text.length) {
    in 0..7 -> 72
    8 -> 66
    9 -> 60
    10 -> 54
    11 -> 49
    12 -> 45
    13 -> 42
    14, 15 -> 38
    else -> 32
}

// ------------------------------------------------------------------- keypad

private data class KeySpec(
    val style: KeyStyle,
    val label: String? = null,
    val icon: ImageVector? = null,
    val contentDescription: String? = null,
    val fontSize: Int = 31,
    val fontWeight: FontWeight = FontWeight.Light,
    val onClick: () -> Unit,
    val onLongClick: (() -> Unit)? = null,
)

@Composable
private fun Keypad(
    systemStyle: Boolean,
    onKey: (Key) -> Unit,
    onQuickSet: () -> Unit,
    onPanic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    fun number(label: String) = KeySpec(
        style = KeyStyle.NUMBER,
        label = label,
        onClick = { onKey(Key.Digit(label.first())) },
    )

    // A dead memory key — visual only, so the pad matches the stock calculator.
    fun memory(label: String) = KeySpec(
        style = KeyStyle.MEMORY,
        label = label,
        fontSize = 20,
        fontWeight = FontWeight.Normal,
        onClick = { /* decorative */ },
    )

    val clearKey = KeySpec(
        style = if (systemStyle) KeyStyle.FUNCTION_ACCENT else KeyStyle.FUNCTION,
        label = "AC",
        fontSize = 24,
        fontWeight = FontWeight.Medium,
        onClick = { onKey(Key.Clear) },
        onLongClick = onPanic,
    )
    val backspaceKey = KeySpec(
        style = if (systemStyle) KeyStyle.FUNCTION_ACCENT else KeyStyle.FUNCTION,
        icon = Icons.Outlined.Backspace,
        contentDescription = "Delete",
        onClick = { onKey(Key.Backspace) },
    )
    val signKey = KeySpec(
        style = if (systemStyle) KeyStyle.FUNCTION_ACCENT else KeyStyle.FUNCTION,
        label = "+/\u2212",
        fontSize = if (systemStyle) 22 else 28,
        onClick = { onKey(Key.Sign) },
    )
    val percentKey = KeySpec(
        style = if (systemStyle) KeyStyle.FUNCTION_ACCENT else KeyStyle.FUNCTION,
        label = "%",
        fontSize = 27,
        onClick = { onKey(Key.Percent) },
    )
    val divideKey = KeySpec(
        style = KeyStyle.OPERATOR,
        label = Ops.DIVIDE.toString(),
        fontSize = 33,
        onClick = { onKey(Key.Op(Ops.DIVIDE)) },
    )
    val timesKey = KeySpec(
        style = KeyStyle.OPERATOR,
        label = Ops.TIMES.toString(),
        fontSize = 33,
        onClick = { onKey(Key.Op(Ops.TIMES)) },
    )
    val minusKey = KeySpec(
        style = KeyStyle.OPERATOR,
        label = Ops.MINUS.toString(),
        fontSize = 35,
        onClick = { onKey(Key.Op(Ops.MINUS)) },
    )
    val plusKey = KeySpec(
        style = KeyStyle.OPERATOR,
        label = Ops.PLUS.toString(),
        fontSize = 33,
        onClick = { onKey(Key.Op(Ops.PLUS)) },
    )
    val dotKey = KeySpec(
        style = KeyStyle.NUMBER,
        label = ".",
        fontSize = 34,
        fontWeight = FontWeight.Medium,
        onClick = { onKey(Key.Dot) },
    )
    val equalsKey = KeySpec(
        style = KeyStyle.EQUALS,
        label = "=",
        fontSize = 34,
        fontWeight = FontWeight.Normal,
        onClick = { onKey(Key.Equals) },
        onLongClick = onQuickSet,
    )

    val rows: List<List<KeySpec>> = if (systemStyle) {
        listOf(
            listOf(memory("mc"), memory("m+"), memory("m\u2212"), memory("mr")),
            listOf(clearKey, backspaceKey, signKey, divideKey),
            listOf(number("7"), number("8"), number("9"), timesKey),
            listOf(number("4"), number("5"), number("6"), minusKey),
            listOf(number("1"), number("2"), number("3"), plusKey),
            listOf(percentKey, number("0"), dotKey, equalsKey),
        )
    } else {
        listOf(
            listOf(clearKey, backspaceKey, percentKey, divideKey),
            listOf(number("7"), number("8"), number("9"), timesKey),
            listOf(number("4"), number("5"), number("6"), minusKey),
            listOf(number("1"), number("2"), number("3"), plusKey),
            listOf(signKey, number("0"), dotKey, equalsKey),
        )
    }

    val keyPadding = if (systemStyle) 7.dp else 6.dp

    Column(modifier = modifier) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                row.forEach { spec ->
                    CalcKey(
                        style = spec.style,
                        label = spec.label,
                        icon = spec.icon,
                        contentDescription = spec.contentDescription,
                        fontSize = spec.fontSize,
                        fontWeight = spec.fontWeight,
                        onClick = spec.onClick,
                        onLongClick = spec.onLongClick,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(keyPadding),
                    )
                }
            }
        }
    }
}
