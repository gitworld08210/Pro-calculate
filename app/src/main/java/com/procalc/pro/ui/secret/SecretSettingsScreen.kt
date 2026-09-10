package com.procalc.pro.ui.secret

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.procalc.pro.magic.ArmGesture
import com.procalc.pro.magic.DateStyle
import com.procalc.pro.magic.ForceMode
import com.procalc.pro.magic.ForceResolver
import com.procalc.pro.magic.ForceTrigger
import com.procalc.pro.magic.MagicSettings
import com.procalc.pro.magic.Palette
import com.procalc.pro.magic.Separator
import com.procalc.pro.magic.TimeStyle
import com.procalc.pro.ui.clickableNoRipple
import com.procalc.pro.ui.theme.LocalCalcPalette
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun SecretSettingsScreen(
    settings: MagicSettings,
    onUpdate: ((MagicSettings) -> MagicSettings) -> Unit,
    onSetArmed: (Boolean) -> Unit,
    onPanic: () -> Unit,
    onOpenGuide: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalCalcPalette.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to palette.backdropCore,
                    0.6f to palette.backdropEdge,
                    1f to palette.backdropEdge,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            TopBar(onClose = onClose, onOpenGuide = onOpenGuide)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 40.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                item { StatusCard(settings = settings, onSetArmed = onSetArmed, onPanic = onPanic) }

                item { EffectCard(settings = settings, onUpdate = onUpdate) }

                if (settings.mode != ForceMode.OFF) {
                    item { TimingCard(settings = settings, onUpdate = onUpdate) }
                    item { ArmingCard(settings = settings, onUpdate = onUpdate) }
                }

                item { AppearanceCard(settings = settings, onUpdate = onUpdate) }

                item { SecurityCard(settings = settings, onUpdate = onUpdate) }
            }
        }
    }
}

// ------------------------------------------------------------------ top bar

@Composable
private fun TopBar(onClose: () -> Unit, onOpenGuide: () -> Unit) {
    val palette = LocalCalcPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Back",
            tint = palette.displayPrimary,
            modifier = Modifier
                .clip(CircleShape)
                .clickableNoRipple(onClose)
                .padding(10.dp),
        )
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Performer",
                color = palette.displayPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "Private controls",
                color = palette.displaySecondary,
                fontSize = 12.sp,
            )
        }
        Icon(
            imageVector = Icons.Outlined.MenuBook,
            contentDescription = "Routines",
            tint = palette.accent,
            modifier = Modifier
                .clip(CircleShape)
                .clickableNoRipple(onOpenGuide)
                .padding(10.dp),
        )
    }
}

// --------------------------------------------------------------- status card

@Composable
private fun StatusCard(
    settings: MagicSettings,
    onSetArmed: (Boolean) -> Unit,
    onPanic: () -> Unit,
) {
    val palette = LocalCalcPalette.current
    val live = settings.isLive

    SectionCard(title = "Status") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (live) palette.accent else palette.displaySecondary.copy(alpha = 0.5f))
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (live) "Armed" else "Honest",
                    color = palette.displayPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = if (live) {
                        "The next reveal will show your forced value."
                    } else if (settings.mode == ForceMode.OFF) {
                        "No effect selected — pure calculator."
                    } else {
                        "Effect ready, waiting to be armed."
                    },
                    color = palette.displaySecondary,
                    fontSize = 12.sp,
                )
            }
        }

        if (settings.mode != ForceMode.OFF) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PrimaryButton(
                    text = if (settings.armed) "Disarm" else "Arm now",
                    filled = !settings.armed,
                    modifier = Modifier.weight(1f),
                    onClick = { onSetArmed(!settings.armed) },
                )
                PrimaryButton(
                    text = "Go honest",
                    filled = false,
                    modifier = Modifier.weight(1f),
                    onClick = onPanic,
                )
            }

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.backdropEdge.copy(alpha = 0.6f))
                    .border(0.7.dp, palette.hairline, RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Text(
                    text = "THEY WILL SEE",
                    color = palette.displaySecondary,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = ForceResolver.preview(settings),
                    color = palette.displayPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    }
}

// --------------------------------------------------------------- effect card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EffectCard(
    settings: MagicSettings,
    onUpdate: ((MagicSettings) -> MagicSettings) -> Unit,
) {
    val palette = LocalCalcPalette.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }

    SectionCard(title = "The effect", subtitle = settings.mode.blurb) {
        ChipGroup(
            options = ForceMode.entries.toList(),
            selected = settings.mode,
            label = { it.label },
            onSelect = { mode -> onUpdate { it.copy(mode = mode) } },
        )

        if (settings.mode == ForceMode.NUMBER) {
            FieldLabel("Number to reveal")
            OutlinedTextField(
                value = settings.forcedNumber,
                onValueChange = { input ->
                    val cleaned = input.filterIndexed { index, c ->
                        c.isDigit() || (c == '.' && !input.take(index).contains('.')) ||
                            (c == '-' && index == 0)
                    }.take(14)
                    onUpdate { it.copy(forcedNumber = cleaned) }
                },
                placeholder = { Text("e.g. 27183460", color = palette.displaySecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            ToggleRow(
                title = "Show digits exactly as typed",
                subtitle = "No thousands separators — right for serial numbers and PINs.",
                checked = settings.rawNumberDisplay,
                onCheckedChange = { value -> onUpdate { it.copy(rawNumberDisplay = value) } },
            )
        }

        val usesDate = settings.mode in setOf(
            ForceMode.CURRENT_DATE, ForceMode.CURRENT_DATE_TIME, ForceMode.PRESET_DATE_TIME,
        )
        val usesTime = settings.mode in setOf(
            ForceMode.CURRENT_TIME, ForceMode.CURRENT_DATE_TIME, ForceMode.PRESET_DATE_TIME,
        )

        if (usesDate) {
            FieldLabel("Date format")
            ChipGroup(
                options = DateStyle.entries.toList(),
                selected = settings.dateStyle,
                label = { it.label },
                onSelect = { style -> onUpdate { it.copy(dateStyle = style) } },
            )
        }

        if (usesTime) {
            FieldLabel("Time format")
            ChipGroup(
                options = TimeStyle.entries.toList(),
                selected = settings.timeStyle,
                label = { it.label },
                onSelect = { style -> onUpdate { it.copy(timeStyle = style) } },
            )
        }

        if (usesDate && usesTime) {
            FieldLabel("Separator between them")
            ChipGroup(
                options = Separator.entries.toList(),
                selected = settings.separator,
                label = { it.label },
                onSelect = { sep -> onUpdate { it.copy(separator = sep) } },
            )
        }

        if (settings.mode == ForceMode.PRESET_DATE_TIME) {
            FieldLabel("Moment to reveal")
            val chosen = if (settings.presetMillis > 0) {
                Instant.ofEpochMilli(settings.presetMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm"))
            } else {
                "Not set"
            }
            Text(text = chosen, color = palette.displayPrimary, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = "Pick date & time",
                filled = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true },
            )
        }
    }

    if (showDatePicker) {
        val initial = if (settings.presetMillis > 0) settings.presetMillis else System.currentTimeMillis()
        val state = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        pickedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        showDatePicker = false
                        showTimePicker = true
                    } else {
                        showDatePicker = false
                    }
                }) { Text("Next", color = palette.accent) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = palette.displaySecondary)
                }
            },
        ) {
            DatePicker(state = state)
        }
    }

    if (showTimePicker) {
        val now = LocalTime.now()
        val state = rememberTimePickerState(
            initialHour = now.hour,
            initialMinute = now.minute,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = pickedDate ?: LocalDate.now()
                    val millis = LocalDateTime.of(date, LocalTime.of(state.hour, state.minute))
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    onUpdate { it.copy(presetMillis = millis) }
                    showTimePicker = false
                }) { Text("Save", color = palette.accent) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = palette.displaySecondary)
                }
            },
            title = { Text("Time", color = palette.displayPrimary) },
            text = { TimePicker(state = state) },
            containerColor = palette.surfaceElevated,
        )
    }
}

// --------------------------------------------------------------- timing card

@Composable
private fun TimingCard(
    settings: MagicSettings,
    onUpdate: ((MagicSettings) -> MagicSettings) -> Unit,
) {
    val palette = LocalCalcPalette.current

    SectionCard(title = "When it fires", subtitle = settings.trigger.blurb) {
        ChipGroup(
            options = ForceTrigger.entries.toList(),
            selected = settings.trigger,
            label = { it.label },
            onSelect = { trigger -> onUpdate { it.copy(trigger = trigger) } },
        )

        if (settings.trigger == ForceTrigger.NTH_EQUALS) {
            FieldLabel("Fire on press number")
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepperButton(text = "\u2212") {
                    onUpdate { it.copy(nth = (it.nth - 1).coerceAtLeast(1)) }
                }
                Text(
                    text = settings.nth.toString(),
                    color = palette.displayPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                StepperButton(text = "+") {
                    onUpdate { it.copy(nth = (it.nth + 1).coerceAtMost(20)) }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        ToggleRow(
            title = "Return to honest after the reveal",
            subtitle = "Recommended. If they keep playing with it, every answer is real.",
            checked = settings.autoDisarm,
            onCheckedChange = { value -> onUpdate { it.copy(autoDisarm = value) } },
        )
    }
}

// -------------------------------------------------------------- arming card

@Composable
private fun ArmingCard(
    settings: MagicSettings,
    onUpdate: ((MagicSettings) -> MagicSettings) -> Unit,
) {
    SectionCard(title = "Secret controls", subtitle = settings.armGesture.blurb) {
        ChipGroup(
            options = ArmGesture.entries.toList(),
            selected = settings.armGesture,
            label = { it.label },
            onSelect = { gesture -> onUpdate { it.copy(armGesture = gesture) } },
        )

        Spacer(Modifier.height(10.dp))
        ToggleRow(
            title = "Long-press = to store a number",
            subtitle = "Type a number, hold =, and it becomes the forced answer. The screen clears itself.",
            checked = settings.quickSetEnabled,
            onCheckedChange = { value -> onUpdate { it.copy(quickSetEnabled = value) } },
        )
        ToggleRow(
            title = "Armed indicator dot",
            subtitle = "A 4-pixel dot top-left while armed. Invisible from a step away.",
            checked = settings.armedIndicator,
            onCheckedChange = { value -> onUpdate { it.copy(armedIndicator = value) } },
        )
        ToggleRow(
            title = "Vibration confirmations",
            subtitle = "One pulse armed, two disarmed, three stored — confirm without looking.",
            checked = settings.haptics,
            onCheckedChange = { value -> onUpdate { it.copy(haptics = value) } },
        )
    }
}

// ----------------------------------------------------------- appearance card

@Composable
private fun AppearanceCard(
    settings: MagicSettings,
    onUpdate: ((MagicSettings) -> MagicSettings) -> Unit,
) {
    SectionCard(title = "Appearance") {
        ChipGroup(
            options = Palette.entries.toList(),
            selected = settings.palette,
            label = { it.label },
            onSelect = { palette -> onUpdate { it.copy(palette = palette) } },
        )
    }
}

// ------------------------------------------------------------- security card

@Composable
private fun SecurityCard(
    settings: MagicSettings,
    onUpdate: ((MagicSettings) -> MagicSettings) -> Unit,
) {
    val palette = LocalCalcPalette.current
    var newPin by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    SectionCard(
        title = "Access code",
        subtitle = "Four digits, asked for whenever this screen is opened.",
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newPin,
                onValueChange = {
                    saved = false
                    newPin = it.filter(Char::isDigit).take(4)
                },
                placeholder = { Text("New code", color = palette.displaySecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                colors = fieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(10.dp))
            PrimaryButton(
                text = if (saved) "Saved" else "Save",
                filled = newPin.length == 4 && !saved,
                onClick = {
                    if (newPin.length == 4) {
                        onUpdate { it.copy(pin = newPin) }
                        newPin = ""
                        saved = true
                    }
                },
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Current code is ${"\u2022".repeat(settings.pin.length)}" +
                if (settings.pin == MagicSettings.DEFAULT_PIN) "  (still the default 1111)" else "",
            color = palette.displaySecondary,
            fontSize = 12.sp,
        )
    }
}

// ------------------------------------------------------------------- pieces

@Composable
private fun PrimaryButton(
    text: String,
    filled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalCalcPalette.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (filled) {
                    Brush.horizontalGradient(listOf(palette.equalsTop, palette.equalsBottom))
                } else {
                    Brush.horizontalGradient(listOf(palette.numberKeyTop, palette.numberKeyBottom))
                }
            )
            .border(
                0.7.dp,
                if (filled) androidx.compose.ui.graphics.Color.Transparent else palette.hairline,
                RoundedCornerShape(50),
            )
            .clickableNoRipple(onClick)
            .padding(horizontal = 20.dp, vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (filled) palette.equalsText else palette.displayPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun StepperButton(text: String, onClick: () -> Unit) {
    val palette = LocalCalcPalette.current
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(palette.numberKeyTop, palette.numberKeyBottom)))
            .border(0.7.dp, palette.hairline, CircleShape)
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = palette.displayPrimary, fontSize = 20.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = LocalCalcPalette.current.displayPrimary,
    unfocusedTextColor = LocalCalcPalette.current.displayPrimary,
    focusedBorderColor = LocalCalcPalette.current.accent,
    unfocusedBorderColor = LocalCalcPalette.current.hairline,
    cursorColor = LocalCalcPalette.current.accent,
    focusedContainerColor = LocalCalcPalette.current.backdropEdge.copy(alpha = 0.5f),
    unfocusedContainerColor = LocalCalcPalette.current.backdropEdge.copy(alpha = 0.5f),
)
