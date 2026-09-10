package com.procalc.pro.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Immutable snapshot of everything the calculator screen needs to render.
 *
 * @param tokens committed expression tokens
 * @param pending the number currently being typed, raw (e.g. "12.", "-3")
 * @param display the large primary line
 * @param expression the small secondary line above the display
 * @param resultShown true when [display] holds an evaluated result rather than typed input
 * @param textResult true when [display] holds non-numeric text (a forced date, for example)
 */
data class CalcState(
    val tokens: List<Token> = emptyList(),
    val pending: String = "",
    val display: String = "0",
    val expression: String = "",
    val isError: Boolean = false,
    val resultShown: Boolean = false,
    val textResult: Boolean = false,
)

/**
 * A pure reducer over [CalcState]. Every key press maps to one function here, which
 * keeps the arithmetic completely independent of the UI and of the performance layer.
 */
object CalculatorEngine {

    private const val MAX_INPUT_DIGITS = 12
    private val MC = MathContext(24, RoundingMode.HALF_UP)

    /** Display text that should replace the honest result. */
    data class Override(val text: String, val numeric: BigDecimal?)

    // ---------------------------------------------------------------- input

    fun digit(state: CalcState, d: Char): CalcState {
        val base = if (state.isError) CalcState() else state
        val fresh = if (base.resultShown) base.copy(tokens = emptyList(), pending = "", resultShown = false, textResult = false) else base

        val current = fresh.pending
        val next = when {
            current.isEmpty() || current == "0" -> if (d == '0') "0" else d.toString()
            current == "-0" -> if (d == '0') "-0" else "-$d"
            digitCount(current) >= MAX_INPUT_DIGITS -> current
            else -> current + d
        }
        return fresh.withInput(next)
    }

    fun dot(state: CalcState): CalcState {
        val base = if (state.isError) CalcState() else state
        val fresh = if (base.resultShown) base.copy(tokens = emptyList(), pending = "", resultShown = false, textResult = false) else base

        val current = fresh.pending
        if (current.contains('.')) return fresh
        val next = if (current.isEmpty() || current == "-") "${current}0." else "$current."
        return fresh.withInput(next)
    }

    fun operator(state: CalcState, symbol: Char): CalcState {
        if (state.isError) return CalcState()

        var tokens = state.tokens.toMutableList()
        var pending = state.pending

        if (state.resultShown) {
            // Chain from the value on screen.
            pending = ""
        } else if (pending.isNotEmpty()) {
            tokens.add(Token.Num(parse(pending)))
            pending = ""
        } else if (tokens.isEmpty()) {
            tokens.add(Token.Num(BigDecimal.ZERO))
        }

        if (tokens.isEmpty()) tokens.add(Token.Num(BigDecimal.ZERO))

        if (tokens.last() is Token.Op) {
            tokens[tokens.lastIndex] = Token.Op(symbol)
        } else {
            tokens.add(Token.Op(symbol))
        }

        return state.copy(
            tokens = tokens,
            pending = "",
            expression = renderExpression(tokens, ""),
            display = state.display,
            resultShown = false,
            textResult = false,
            isError = false,
        )
    }

    fun toggleSign(state: CalcState): CalcState {
        if (state.isError) return state
        if (state.pending.isNotEmpty()) {
            val next = if (state.pending.startsWith("-")) state.pending.substring(1) else "-${state.pending}"
            return state.withInput(next)
        }
        if (state.resultShown && !state.textResult) {
            val negated = currentValue(state)?.negate() ?: return state
            return state.copy(
                tokens = listOf(Token.Num(negated)),
                display = NumberFormatter.format(negated),
            )
        }
        return state
    }

    fun percent(state: CalcState): CalcState {
        if (state.isError) return state
        if (state.pending.isNotEmpty()) {
            val scaled = parse(state.pending).divide(BigDecimal(100), MC)
            return state.copy(
                pending = scaled.stripTrailingZeros().toPlainString(),
                display = NumberFormatter.format(scaled),
                expression = renderExpression(state.tokens, scaled.stripTrailingZeros().toPlainString()),
                resultShown = false,
                textResult = false,
            )
        }
        if (state.resultShown && !state.textResult) {
            val scaled = (currentValue(state) ?: return state).divide(BigDecimal(100), MC)
            return state.copy(
                tokens = listOf(Token.Num(scaled)),
                display = NumberFormatter.format(scaled),
            )
        }
        return state
    }

    fun backspace(state: CalcState): CalcState {
        if (state.isError) return CalcState()
        if (state.resultShown) return CalcState()

        if (state.pending.isNotEmpty()) {
            val next = state.pending.dropLast(1)
            return if (next.isEmpty() || next == "-") state.withInput("") else state.withInput(next)
        }

        val tokens = state.tokens.toMutableList()
        if (tokens.isEmpty()) return state

        return when (val last = tokens.removeAt(tokens.lastIndex)) {
            is Token.Op -> state.copy(tokens = tokens, expression = renderExpression(tokens, ""))
            is Token.Num -> {
                val raw = last.value.stripTrailingZeros().toPlainString()
                state.copy(
                    tokens = tokens,
                    pending = raw,
                    display = NumberFormatter.formatInput(raw),
                    expression = renderExpression(tokens, raw),
                )
            }
        }
    }

    fun clear(): CalcState = CalcState()

    // ------------------------------------------------------------- evaluate

    fun equals(state: CalcState, override: Override? = null): CalcState {
        if (state.isError) return CalcState()

        val tokens = commit(state)
        if (tokens.isEmpty()) return state

        val expressionSnapshot = renderExpression(tokens, "")

        if (override != null) {
            return CalcState(
                tokens = override.numeric?.let { listOf(Token.Num(it)) } ?: emptyList(),
                pending = "",
                display = override.text,
                expression = expressionSnapshot,
                isError = false,
                resultShown = true,
                textResult = override.numeric == null,
            )
        }

        return when (val result = Evaluator.evaluate(tokens)) {
            is Evaluator.Result.Ok -> CalcState(
                tokens = listOf(Token.Num(result.value)),
                pending = "",
                display = NumberFormatter.format(result.value),
                expression = expressionSnapshot,
                resultShown = true,
            )
            is Evaluator.Result.Err -> CalcState(
                tokens = emptyList(),
                pending = "",
                display = result.message,
                expression = expressionSnapshot,
                isError = true,
                resultShown = true,
                textResult = true,
            )
        }
    }

    /** The honest numeric result of the current expression, or null if it cannot be evaluated. */
    fun honestResult(state: CalcState): BigDecimal? {
        val tokens = commit(state)
        if (tokens.isEmpty()) return null
        return (Evaluator.evaluate(tokens) as? Evaluator.Result.Ok)?.value
    }

    /** The number currently on the display, if the display holds a number. */
    fun currentValue(state: CalcState): BigDecimal? {
        if (state.pending.isNotEmpty()) return parse(state.pending)
        if (state.textResult || state.isError) return null
        return (state.tokens.lastOrNull() as? Token.Num)?.value
    }

    // -------------------------------------------------------------- helpers

    private fun commit(state: CalcState): List<Token> {
        val tokens = state.tokens.toMutableList()
        if (state.pending.isNotEmpty()) {
            tokens.add(Token.Num(parse(state.pending)))
        }
        // "5 +" then "=" behaves as "5".
        while (tokens.isNotEmpty() && tokens.last() is Token.Op) {
            tokens.removeAt(tokens.lastIndex)
        }
        return tokens
    }

    private fun CalcState.withInput(raw: String): CalcState = copy(
        pending = raw,
        display = NumberFormatter.formatInput(raw),
        expression = renderExpression(tokens, raw),
        isError = false,
        resultShown = false,
        textResult = false,
    )

    fun renderExpression(tokens: List<Token>, pending: String): String {
        if (tokens.isEmpty() && pending.isEmpty()) return ""
        val sb = StringBuilder()
        tokens.forEach { token ->
            when (token) {
                is Token.Num -> sb.append(NumberFormatter.format(token.value))
                is Token.Op -> sb.append(' ').append(token.symbol).append(' ')
            }
        }
        if (pending.isNotEmpty()) sb.append(NumberFormatter.formatInput(pending))
        return sb.toString().trimEnd()
    }

    private fun digitCount(raw: String): Int = raw.count { it.isDigit() }

    fun parse(raw: String): BigDecimal {
        if (raw.isEmpty() || raw == "-") return BigDecimal.ZERO
        val cleaned = raw.removeSuffix(".")
        if (cleaned.isEmpty() || cleaned == "-") return BigDecimal.ZERO
        return cleaned.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }
}
