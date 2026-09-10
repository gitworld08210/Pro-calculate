package com.procalc.pro.magic

import com.procalc.pro.engine.CalculatorEngine
import com.procalc.pro.engine.NumberFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Turns the configured [MagicSettings] into the exact string that should appear
 * on the display in place of the honest answer.
 */
object ForceResolver {

    /**
     * @return the display override, or null when there is nothing valid to force
     *         (in which case the calculator stays honest — failing safe).
     */
    fun resolve(
        settings: MagicSettings,
        now: LocalDateTime = LocalDateTime.now(),
    ): CalculatorEngine.Override? = when (settings.mode) {
        ForceMode.OFF -> null

        ForceMode.NUMBER -> {
            val raw = settings.forcedNumber.trim()
            if (raw.isEmpty()) null else {
                val numeric = CalculatorEngine.parse(raw)
                val text = if (settings.rawNumberDisplay) raw else NumberFormatter.group(raw)
                CalculatorEngine.Override(text = text, numeric = numeric)
            }
        }

        ForceMode.CURRENT_DATE -> textOverride(format(now, settings.dateStyle.pattern))

        ForceMode.CURRENT_TIME -> textOverride(format(now, settings.timeStyle.pattern))

        ForceMode.CURRENT_DATE_TIME -> textOverride(
            format(now, settings.dateStyle.pattern) +
                settings.separator.text +
                format(now, settings.timeStyle.pattern)
        )

        ForceMode.PRESET_DATE_TIME -> {
            if (settings.presetMillis <= 0L) null else {
                val preset = Instant.ofEpochMilli(settings.presetMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                textOverride(
                    format(preset, settings.dateStyle.pattern) +
                        settings.separator.text +
                        format(preset, settings.timeStyle.pattern)
                )
            }
        }
    }

    /** A live preview of what the audience will see, for the settings screen. */
    fun preview(settings: MagicSettings): String =
        resolve(settings)?.text ?: "—"

    /**
     * Date and time strings are not arithmetic, so they are marked as text results:
     * the next key press starts a clean calculation instead of chaining nonsense.
     */
    private fun textOverride(text: String) = CalculatorEngine.Override(text = text, numeric = null)

    private fun format(dateTime: LocalDateTime, pattern: String): String =
        dateTime.format(DateTimeFormatter.ofPattern(pattern))
}
