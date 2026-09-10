package com.procalc.pro

import com.procalc.pro.engine.CalcState
import com.procalc.pro.engine.CalculatorEngine
import com.procalc.pro.engine.NumberFormatter
import com.procalc.pro.engine.Ops
import com.procalc.pro.magic.ForceMode
import com.procalc.pro.magic.ForceResolver
import com.procalc.pro.magic.MagicSettings
import com.procalc.pro.magic.TimeStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class CalculatorLogicTest {

    /** Types a sequence like "12+3" and returns the resulting state. */
    private fun type(input: String): CalcState {
        var state = CalcState()
        input.forEach { c ->
            state = when {
                c.isDigit() -> CalculatorEngine.digit(state, c)
                c == '.' -> CalculatorEngine.dot(state)
                c == '+' -> CalculatorEngine.operator(state, Ops.PLUS)
                c == '-' -> CalculatorEngine.operator(state, Ops.MINUS)
                c == '*' -> CalculatorEngine.operator(state, Ops.TIMES)
                c == '/' -> CalculatorEngine.operator(state, Ops.DIVIDE)
                else -> state
            }
        }
        return state
    }

    private fun result(input: String): String =
        CalculatorEngine.equals(type(input)).display

    // ------------------------------------------------------------ arithmetic

    @Test
    fun `respects operator precedence`() {
        assertEquals("14", result("2+3*4"))
        assertEquals("10", result("2*3+4"))
        assertEquals("7", result("1+2*3"))
    }

    @Test
    fun `chains additions left to right`() {
        assertEquals("6", result("1+2+3"))
        assertEquals("0", result("5-3-2"))
    }

    @Test
    fun `divides exactly and repeats safely`() {
        assertEquals("2.5", result("5/2"))
        assertTrue(result("1/3").startsWith("0.333333"))
    }

    @Test
    fun `reports division by zero instead of crashing`() {
        val state = CalculatorEngine.equals(type("8/0"))
        assertTrue(state.isError)
        assertEquals("Cannot divide by zero", state.display)
    }

    @Test
    fun `trailing operator is ignored`() {
        assertEquals("5", result("5+"))
    }

    @Test
    fun `percent divides by one hundred`() {
        val state = CalculatorEngine.percent(type("50"))
        assertEquals("0.5", state.display)
    }

    @Test
    fun `sign toggles on the number being typed`() {
        val state = CalculatorEngine.toggleSign(type("42"))
        assertEquals("-42", state.display)
    }

    @Test
    fun `backspace removes one character then falls back to tokens`() {
        var state = type("12+34")
        state = CalculatorEngine.backspace(state)
        assertEquals("3", state.display)
        state = CalculatorEngine.backspace(state)   // clears "3"
        state = CalculatorEngine.backspace(state)   // drops the "+"
        assertEquals("12", CalculatorEngine.equals(state).display)
    }

    @Test
    fun `result can be chained into a new calculation`() {
        var state = CalculatorEngine.equals(type("2+3"))
        assertEquals("5", state.display)
        state = CalculatorEngine.operator(state, Ops.TIMES)
        state = CalculatorEngine.digit(state, '4')
        assertEquals("20", CalculatorEngine.equals(state).display)
    }

    // ------------------------------------------------------------ formatting

    @Test
    fun `groups thousands like a real calculator`() {
        assertEquals("1,234,567", result("1234567"))
        assertEquals("12,345.5", result("12345.5"))
    }

    @Test
    fun `formats large magnitudes in scientific notation`() {
        val huge = result("99999999999*99999999999")
        assertTrue("was $huge", huge.contains("e"))
    }

    @Test
    fun `raw grouping helper leaves short digit strings untouched`() {
        assertEquals("123", NumberFormatter.group("123"))
        assertEquals("27,183,460", NumberFormatter.group("27183460"))
    }

    // ----------------------------------------------------------- the force

    @Test
    fun `number force replaces the honest answer`() {
        val settings = MagicSettings(
            mode = ForceMode.NUMBER,
            forcedNumber = "27183460",
            armed = true,
        )
        val override = ForceResolver.resolve(settings)!!
        val state = CalculatorEngine.equals(type("123*456"), override)

        assertEquals("27,183,460", state.display)
        // The honest answer must not survive anywhere on screen.
        assertEquals("123 \u00D7 456", state.expression)
    }

    @Test
    fun `raw display keeps serial numbers unformatted`() {
        val settings = MagicSettings(
            mode = ForceMode.NUMBER,
            forcedNumber = "27183460",
            rawNumberDisplay = true,
            armed = true,
        )
        assertEquals("27183460", ForceResolver.resolve(settings)!!.text)
    }

    @Test
    fun `forced number stays usable for further arithmetic`() {
        val override = ForceResolver.resolve(
            MagicSettings(mode = ForceMode.NUMBER, forcedNumber = "100", armed = true)
        )!!
        var state = CalculatorEngine.equals(type("7*7"), override)
        assertEquals("100", state.display)

        state = CalculatorEngine.operator(state, Ops.PLUS)
        state = CalculatorEngine.digit(state, '5')
        assertEquals("105", CalculatorEngine.equals(state).display)
    }

    @Test
    fun `date force renders the chosen pattern`() {
        val moment = LocalDateTime.of(2026, 9, 10, 14, 5)
        val settings = MagicSettings(mode = ForceMode.CURRENT_DATE, armed = true)
        assertEquals("10.09.2026", ForceResolver.resolve(settings, moment)!!.text)
    }

    @Test
    fun `time force renders to the minute`() {
        val moment = LocalDateTime.of(2026, 9, 10, 14, 5)
        val settings = MagicSettings(
            mode = ForceMode.CURRENT_TIME,
            timeStyle = TimeStyle.H24_PLAIN,
            armed = true,
        )
        assertEquals("1405", ForceResolver.resolve(settings, moment)!!.text)
    }

    @Test
    fun `date and time force joins with the chosen separator`() {
        val moment = LocalDateTime.of(2026, 9, 10, 14, 5)
        val settings = MagicSettings(mode = ForceMode.CURRENT_DATE_TIME, armed = true)
        assertEquals("10.09.2026 14.05", ForceResolver.resolve(settings, moment)!!.text)
    }

    @Test
    fun `dates are marked as text so the next key press starts clean`() {
        val moment = LocalDateTime.of(2026, 9, 10, 14, 5)
        val override = ForceResolver.resolve(
            MagicSettings(mode = ForceMode.CURRENT_DATE, armed = true), moment
        )!!
        assertNull(override.numeric)

        var state = CalculatorEngine.equals(type("2+2"), override)
        assertTrue(state.textResult)

        state = CalculatorEngine.digit(state, '9')
        assertEquals("9", state.display)
    }

    // -------------------------------------------------------------- failsafe

    @Test
    fun `off mode never forces anything`() {
        assertNull(ForceResolver.resolve(MagicSettings(mode = ForceMode.OFF, armed = true)))
    }

    @Test
    fun `an empty forced number falls back to honest arithmetic`() {
        val settings = MagicSettings(mode = ForceMode.NUMBER, forcedNumber = "", armed = true)
        assertNull(ForceResolver.resolve(settings))
    }

    @Test
    fun `an unset preset moment falls back to honest arithmetic`() {
        val settings = MagicSettings(mode = ForceMode.PRESET_DATE_TIME, presetMillis = 0, armed = true)
        assertNull(ForceResolver.resolve(settings))
    }

    @Test
    fun `isLive requires both an effect and an armed app`() {
        assertTrue(MagicSettings(mode = ForceMode.NUMBER, armed = true).isLive)
        assertTrue(!MagicSettings(mode = ForceMode.NUMBER, armed = false).isLive)
        assertTrue(!MagicSettings(mode = ForceMode.OFF, armed = true).isLive)
    }
}
