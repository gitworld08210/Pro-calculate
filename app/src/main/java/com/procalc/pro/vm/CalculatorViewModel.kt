package com.procalc.pro.vm

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.procalc.pro.engine.CalcState
import com.procalc.pro.engine.CalculatorEngine
import com.procalc.pro.magic.ForceMode
import com.procalc.pro.magic.ForceResolver
import com.procalc.pro.magic.ForceTrigger
import com.procalc.pro.magic.MagicSettings
import com.procalc.pro.magic.MagicSettingsStore

/** Every key on the pad. */
sealed interface Key {
    data class Digit(val d: Char) : Key
    data class Op(val symbol: Char) : Key
    data object Dot : Key
    data object Equals : Key
    data object Clear : Key
    data object Backspace : Key
    data object Percent : Key
    data object Sign : Key
}

/** Result of a secret gesture, so the UI knows which haptic to fire. */
enum class ArmFeedback { ARMED, DISARMED, STORED, IGNORED }

class CalculatorViewModel(app: Application) : AndroidViewModel(app) {

    private val store = MagicSettingsStore.get(app)
    val settings = store.settings

    var calc by mutableStateOf(CalcState())
        private set

    /** How many times = has been pressed since the app was armed. */
    private var equalsSinceArmed = 0

    // ------------------------------------------------------------ key input

    fun onKey(key: Key) {
        calc = when (key) {
            is Key.Digit -> CalculatorEngine.digit(calc, key.d)
            is Key.Op -> CalculatorEngine.operator(calc, key.symbol)
            Key.Dot -> CalculatorEngine.dot(calc)
            Key.Clear -> CalculatorEngine.clear()
            Key.Backspace -> CalculatorEngine.backspace(calc)
            Key.Percent -> CalculatorEngine.percent(calc)
            Key.Sign -> CalculatorEngine.toggleSign(calc)
            Key.Equals -> return handleEquals()
        }
    }

    private fun handleEquals() {
        val s = settings.value

        if (!s.isLive) {
            calc = CalculatorEngine.equals(calc)
            return
        }

        equalsSinceArmed++

        val shouldFire = when (s.trigger) {
            ForceTrigger.FIRST_EQUALS -> equalsSinceArmed == 1
            ForceTrigger.NTH_EQUALS -> equalsSinceArmed == s.nth.coerceAtLeast(1)
            ForceTrigger.EVERY_EQUALS -> true
        }

        // Fail safe: anything unresolvable falls back to honest arithmetic rather
        // than showing a blank or broken display in front of an audience.
        val override = if (shouldFire) ForceResolver.resolve(s) else null

        calc = CalculatorEngine.equals(calc, override)

        if (override != null && s.autoDisarm) {
            setArmed(false)
        }
    }

    // ------------------------------------------------------- secret controls

    /** Long-press on = : store what is on screen as the forced number and arm. */
    fun quickSetForce(): ArmFeedback {
        val s = settings.value
        if (!s.quickSetEnabled) return ArmFeedback.IGNORED

        val raw = calc.pending.ifEmpty {
            CalculatorEngine.currentValue(calc)?.stripTrailingZeros()?.toPlainString().orEmpty()
        }
        if (raw.isEmpty() || CalculatorEngine.parse(raw).signum() == 0 && raw.none { it.isDigit() }) {
            return ArmFeedback.IGNORED
        }

        store.update {
            it.copy(mode = ForceMode.NUMBER, forcedNumber = raw, armed = true)
        }
        equalsSinceArmed = 0
        // Wipe the screen so there is no trace of what was just stored.
        calc = CalculatorEngine.clear()
        return ArmFeedback.STORED
    }

    /** The invisible corner gesture: arm or disarm the configured routine. */
    fun toggleArmed(): ArmFeedback {
        val s = settings.value
        if (s.mode == ForceMode.OFF) return ArmFeedback.IGNORED
        val next = !s.armed
        setArmed(next)
        return if (next) ArmFeedback.ARMED else ArmFeedback.DISARMED
    }

    fun setArmed(armed: Boolean) {
        equalsSinceArmed = 0
        store.update { it.copy(armed = armed) }
    }

    /** Instantly return to a completely honest calculator. */
    fun panic() {
        equalsSinceArmed = 0
        store.update { it.copy(armed = false) }
        calc = CalculatorEngine.clear()
    }

    fun updateSettings(transform: (MagicSettings) -> MagicSettings) {
        store.update(transform)
        equalsSinceArmed = 0
    }

    fun verifyPin(entered: String): Boolean = entered == settings.value.pin
}
