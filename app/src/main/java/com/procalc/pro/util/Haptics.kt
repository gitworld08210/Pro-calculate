package com.procalc.pro.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Small, distinct vibration patterns. The confirmation patterns exist so the performer
 * can tell — by feel alone, phone in pocket or hand — whether the app just armed or disarmed.
 */
class Haptics(context: Context) {

    private val vibrator: Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    /** A light tick for ordinary key presses. */
    fun tick(enabled: Boolean) {
        if (!enabled) return
        play(VibrationEffect.createOneShot(12, 60))
    }

    /** One firm pulse — "armed". */
    fun armed(enabled: Boolean) {
        if (!enabled) return
        play(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Two short pulses — "disarmed / honest". */
    fun disarmed(enabled: Boolean) {
        if (!enabled) return
        play(VibrationEffect.createWaveform(longArrayOf(0, 35, 90, 35), -1))
    }

    /** Three quick pulses — "stored and armed" after a quick-set. */
    fun stored(enabled: Boolean) {
        if (!enabled) return
        play(VibrationEffect.createWaveform(longArrayOf(0, 30, 70, 30, 70, 30), -1))
    }

    private fun play(effect: VibrationEffect) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        runCatching { v.vibrate(effect) }
    }
}
