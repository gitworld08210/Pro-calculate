package com.procalc.pro.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/** A single element of an expression: either a number or a binary operator. */
sealed interface Token {
    data class Num(val value: BigDecimal) : Token
    data class Op(val symbol: Char) : Token
}

/** Operator glyphs used throughout the app. */
object Ops {
    const val PLUS = '+'
    const val MINUS = '\u2212'   // −  (true minus sign)
    const val TIMES = '\u00D7'   // ×
    const val DIVIDE = '\u00F7'  // ÷

    val all = charArrayOf(PLUS, MINUS, TIMES, DIVIDE)
    fun isOp(c: Char) = c in all
}

/**
 * Evaluates a flat token list honouring standard operator precedence
 * (× and ÷ bind tighter than + and −), left-to-right within a precedence level.
 */
object Evaluator {

    const val ERR_DIVIDE_BY_ZERO = "Cannot divide by zero"
    const val ERR_INVALID = "Invalid expression"

    private val MC = MathContext(24, RoundingMode.HALF_UP)

    sealed interface Result {
        data class Ok(val value: BigDecimal) : Result
        data class Err(val message: String) : Result
    }

    fun evaluate(tokens: List<Token>): Result {
        if (tokens.isEmpty()) return Result.Ok(BigDecimal.ZERO)

        val first = tokens.first() as? Token.Num ?: return Result.Err(ERR_INVALID)

        // Pass 1 — collapse × and ÷ as we walk, queueing + and − for pass 2.
        val values = ArrayDeque<BigDecimal>()
        val additive = ArrayDeque<Char>()
        values.addLast(first.value)

        var i = 1
        while (i < tokens.size) {
            val op = (tokens[i] as? Token.Op)?.symbol ?: return Result.Err(ERR_INVALID)
            val rhs = (tokens.getOrNull(i + 1) as? Token.Num)?.value
                ?: return Result.Err(ERR_INVALID)

            when (op) {
                Ops.TIMES -> values.addLast(values.removeLast().multiply(rhs, MC))
                Ops.DIVIDE -> {
                    if (rhs.signum() == 0) return Result.Err(ERR_DIVIDE_BY_ZERO)
                    values.addLast(values.removeLast().divide(rhs, MC))
                }
                Ops.PLUS, Ops.MINUS -> {
                    additive.addLast(op)
                    values.addLast(rhs)
                }
                else -> return Result.Err(ERR_INVALID)
            }
            i += 2
        }

        // Pass 2 — fold the additive chain left-to-right.
        var acc = values.removeFirst()
        while (additive.isNotEmpty()) {
            val op = additive.removeFirst()
            val rhs = values.removeFirst()
            acc = if (op == Ops.PLUS) acc.add(rhs, MC) else acc.subtract(rhs, MC)
        }

        return Result.Ok(acc)
    }
}
