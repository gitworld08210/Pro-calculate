package com.procalc.pro.magic

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists the performer's configuration.
 *
 * Deliberately stored under an innocuous file name with innocuous keys so nothing
 * gives the routine away if someone goes poking through app storage.
 */
class MagicSettingsStore private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("calc_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(read())
    val settings: StateFlow<MagicSettings> = _settings.asStateFlow()

    fun update(transform: (MagicSettings) -> MagicSettings) {
        val next = transform(_settings.value)
        write(next)
        _settings.value = next
    }

    // ------------------------------------------------------------------ io

    private fun read(): MagicSettings {
        val defaults = MagicSettings()
        return MagicSettings(
            mode = prefs.enum("k_m", defaults.mode),
            trigger = prefs.enum("k_t", defaults.trigger),
            nth = prefs.getInt("k_n", defaults.nth),
            armed = prefs.getBoolean("k_a", defaults.armed),
            forcedNumber = prefs.getString("k_fn", defaults.forcedNumber) ?: defaults.forcedNumber,
            rawNumberDisplay = prefs.getBoolean("k_rn", defaults.rawNumberDisplay),
            dateStyle = prefs.enum("k_ds", defaults.dateStyle),
            timeStyle = prefs.enum("k_ts", defaults.timeStyle),
            separator = prefs.enum("k_sp", defaults.separator),
            presetMillis = prefs.getLong("k_pm", defaults.presetMillis),
            autoDisarm = prefs.getBoolean("k_ad", defaults.autoDisarm),
            armedIndicator = prefs.getBoolean("k_ai", defaults.armedIndicator),
            haptics = prefs.getBoolean("k_hp", defaults.haptics),
            armGesture = prefs.enum("k_ag", defaults.armGesture),
            quickSetEnabled = prefs.getBoolean("k_qs", defaults.quickSetEnabled),
            pin = prefs.getString("k_pin", defaults.pin) ?: defaults.pin,
            palette = prefs.enum("k_pal", defaults.palette),
        )
    }

    private fun write(s: MagicSettings) {
        prefs.edit().apply {
            putString("k_m", s.mode.name)
            putString("k_t", s.trigger.name)
            putInt("k_n", s.nth)
            putBoolean("k_a", s.armed)
            putString("k_fn", s.forcedNumber)
            putBoolean("k_rn", s.rawNumberDisplay)
            putString("k_ds", s.dateStyle.name)
            putString("k_ts", s.timeStyle.name)
            putString("k_sp", s.separator.name)
            putLong("k_pm", s.presetMillis)
            putBoolean("k_ad", s.autoDisarm)
            putBoolean("k_ai", s.armedIndicator)
            putBoolean("k_hp", s.haptics)
            putString("k_ag", s.armGesture.name)
            putBoolean("k_qs", s.quickSetEnabled)
            putString("k_pin", s.pin)
            putString("k_pal", s.palette.name)
        }.apply()
    }

    private inline fun <reified T : Enum<T>> SharedPreferences.enum(key: String, fallback: T): T {
        val stored = getString(key, null) ?: return fallback
        return runCatching { enumValueOf<T>(stored) }.getOrDefault(fallback)
    }

    companion object {
        @Volatile
        private var instance: MagicSettingsStore? = null

        fun get(context: Context): MagicSettingsStore =
            instance ?: synchronized(this) {
                instance ?: MagicSettingsStore(context).also { instance = it }
            }
    }
}
