package com.procalc.pro.ui.secret

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.procalc.pro.ui.clickableNoRipple
import com.procalc.pro.ui.theme.LocalCalcPalette

/** A grouped panel with a small uppercase heading — the backbone of the settings layout. */
@Composable
fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalCalcPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            color = palette.accent.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.6.sp,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(palette.surfaceElevated, palette.surface)
                    )
                )
                .border(0.7.dp, palette.hairline, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = palette.displaySecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            content()
        }
    }
}

/** Selectable pills. Wraps onto multiple lines so long option sets stay readable. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChipGroup(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalCalcPalette.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) {
                            Brush.horizontalGradient(listOf(palette.equalsTop, palette.equalsBottom))
                        } else {
                            Brush.horizontalGradient(listOf(palette.numberKeyTop, palette.numberKeyBottom))
                        }
                    )
                    .border(
                        0.7.dp,
                        if (isSelected) Color.Transparent else palette.hairline,
                        RoundedCornerShape(50),
                    )
                    .clickableNoRipple { onSelect(option) }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
            ) {
                Text(
                    text = label(option),
                    color = if (isSelected) palette.equalsText else palette.numberText.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun ToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val palette = LocalCalcPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, color = palette.displayPrimary, fontSize = 15.sp)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = palette.displaySecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.equalsText,
                checkedTrackColor = palette.accent,
                uncheckedThumbColor = palette.displaySecondary,
                uncheckedTrackColor = palette.surface,
                uncheckedBorderColor = palette.hairline,
            ),
        )
    }
}

@Composable
fun FieldLabel(text: String) {
    val palette = LocalCalcPalette.current
    Text(
        text = text,
        color = palette.displaySecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
    )
}

@Composable
fun SectionSpacer() = Spacer(modifier = Modifier.height(22.dp))
