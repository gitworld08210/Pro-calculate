package com.procalc.pro.magic

/** What value replaces the honest answer when the force fires. */
enum class ForceMode(val label: String, val blurb: String) {
    OFF("Off", "The calculator is completely honest."),
    NUMBER("Number", "Reveal a number you set in advance."),
    CURRENT_DATE("Today's date", "Reveal the exact date of the performance."),
    CURRENT_TIME("Current time", "Reveal the exact time, to the minute."),
    CURRENT_DATE_TIME("Date + time", "Reveal both date and time of this moment."),
    PRESET_DATE_TIME("Chosen date/time", "Reveal a specific date and time you pick.");
}

/** When the force fires, counted from the moment the app is armed. */
enum class ForceTrigger(val label: String, val blurb: String) {
    FIRST_EQUALS("First =", "Fires the very next time = is pressed."),
    NTH_EQUALS("Nth =", "Let them do honest sums first, then fire."),
    EVERY_EQUALS("Every =", "Every = shows the forced value.");
}

enum class DateStyle(val label: String, val pattern: String) {
    DMY_DOT_LONG("31.12.2026", "dd.MM.yyyy"),
    DMY_DOT_SHORT("31.12.26", "dd.MM.yy"),
    DMY_PLAIN_LONG("31122026", "ddMMyyyy"),
    DMY_PLAIN_SHORT("311226", "ddMMyy"),
    MDY_DOT_LONG("12.31.2026", "MM.dd.yyyy"),
    MDY_PLAIN_LONG("12312026", "MMddyyyy"),
    YMD_PLAIN("20261231", "yyyyMMdd"),
    DM_DOT("31.12", "dd.MM"),
}

enum class TimeStyle(val label: String, val pattern: String) {
    H24_DOT("14.05", "HH.mm"),
    H24_PLAIN("1405", "HHmm"),
    H24_COLON("14:05", "HH:mm"),
    H12_DOT("2.05", "h.mm"),
    H24_SECONDS("14.05.09", "HH.mm.ss"),
}

enum class Separator(val label: String, val text: String) {
    SPACE("Space", " "),
    DOT("Dot", "."),
    DASH("Dash", "-"),
    NONE("None", ""),
}

enum class Palette(val label: String) {
    SYSTEM_RED("System (Red)"),
    OBSIDIAN_GOLD("Obsidian & Gold"),
    MIDNIGHT_PLATINUM("Midnight Platinum"),
    STEALTH_GRAPHITE("Stealth Graphite"),
}

/** How the performer arms the force without touching the settings screen. */
enum class ArmGesture(val label: String, val blurb: String) {
    DOUBLE_TAP_TOP_RIGHT("Double-tap top-right", "Double-tap the invisible top-right corner of the display."),
    DOUBLE_TAP_TOP_LEFT("Double-tap top-left", "Double-tap the invisible top-left corner of the display."),
    LONG_PRESS_DISPLAY("Long-press the display", "Press and hold anywhere on the number line."),
}

/**
 * Everything the performer configures. Persisted verbatim so a routine survives
 * app restarts — you can set up hours before you perform.
 */
data class MagicSettings(
    val mode: ForceMode = ForceMode.OFF,
    val trigger: ForceTrigger = ForceTrigger.FIRST_EQUALS,
    val nth: Int = 2,
    val armed: Boolean = false,

    /** Digits (and optional decimal point) to force in [ForceMode.NUMBER]. */
    val forcedNumber: String = "",
    /** Show the forced number exactly as typed, without thousands separators. */
    val rawNumberDisplay: Boolean = false,

    val dateStyle: DateStyle = DateStyle.DMY_DOT_LONG,
    val timeStyle: TimeStyle = TimeStyle.H24_DOT,
    val separator: Separator = Separator.SPACE,
    /** Epoch millis for [ForceMode.PRESET_DATE_TIME]. */
    val presetMillis: Long = 0L,

    /** Return to honest mode the instant the force has fired. Strongly recommended. */
    val autoDisarm: Boolean = true,
    /** A near-invisible dot confirming the app is armed. */
    val armedIndicator: Boolean = true,
    val haptics: Boolean = true,

    val armGesture: ArmGesture = ArmGesture.DOUBLE_TAP_TOP_RIGHT,
    /** Long-press = to store whatever is on screen as the forced number and arm. */
    val quickSetEnabled: Boolean = true,

    val pin: String = DEFAULT_PIN,
    val palette: Palette = Palette.SYSTEM_RED,
) {
    val isLive: Boolean get() = mode != ForceMode.OFF && armed

    companion object {
        const val DEFAULT_PIN = "1111"
    }
}
