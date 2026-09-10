package com.procalc.pro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Formats numbers the way a physical calculator does: thousands grouping,
 * a bounded number of significant digits, and scientific notation at the extremes.
 */
object NumberFormatter {

    /** Total digits we are willing to render before switching to scientific notation. */
    private const val MAX_DIGITS = 12

    private val UPPER = BigDecimal("1E+13")
    private val LOWER = BigDecimal("1E-9")

    fun format(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        val abs = stripped.abs()

        if (abs.signum() != 0 && (abs >= UPPER || abs < LOWER)) {
            return scientific(stripped)
        }

        var v = stripped
        if (v.scale() > 0) {
            val intDigits = (v.precision() - v.scale()).coerceAtLeast(1)
            val allowedDecimals = (MAX_DIGITS - intDigits).coerceIn(0, 10)
            v = v.setScale(allowedDecimals, RoundingMode.HALF_UP).stripTrailingZeros()
        }

        if (v.signum() == 0) return "0"
        return group(v.toPlainString())
    }

    /** Formats a raw, partially-typed string such as "1234." or "-0.50". */
    fun formatInput(raw: String): String {
        if (raw.isEmpty()) return "0"
        val negative = raw.startsWith("-")
        val body = if (negative) raw.substring(1) else raw

        val dot = body.indexOf('.')
        val intPart = if (dot >= 0) body.substring(0, dot) else body
        val fracPart = if (dot >= 0) body.substring(dot) else ""   // keeps the trailing dot

        val grouped = groupIntegerPart(intPart.ifEmpty { "0" })
        return buildString {
            if (negative) append('-')
            append(grouped)
            append(fracPart)
        }
    }

    /** Inserts thousands separators into an already-plain decimal string. */
    fun group(plain: String): String {
        val negative = plain.startsWith("-")
        val body = if (negative) plain.substring(1) else plain
        val dot = body.indexOf('.')
        val intPart = if (dot >= 0) body.substring(0, dot) else body
        val fracPart = if (dot >= 0) body.substring(dot) else ""
        return buildString {
            if (negative) append('-')
            append(groupIntegerPart(intPart))
            append(fracPart)
        }
    }

    private fun groupIntegerPart(digits: String): String {
        if (digits.length <= 3) return digits
        val sb = StringBuilder()
        val firstGroup = digits.length % 3
        if (firstGroup > 0) sb.append(digits, 0, firstGroup)
        var i = firstGroup
        while (i < digits.length) {
            if (sb.isNotEmpty()) sb.append(',')
            sb.append(digits, i, i + 3)
            i += 3
        }
        return sb.toString()
    }

    private fun scientific(value: BigDecimal): String {
        val rounded = value.round(java.math.MathContext(8, RoundingMode.HALF_UP))
        val unscaled = rounded.unscaledValue().toString().trimStart('-')
        val exponent = unscaled.length - rounded.scale() - 1
        val mantissaDigits = unscaled.trimEnd('0').ifEmpty { "0" }
        val mantissa = buildString {
            if (rounded.signum() < 0) append('-')
            append(mantissaDigits.first())
            if (mantissaDigits.length > 1) {
                append('.')
                append(mantissaDigits.substring(1))
            }
        }
        return "${mantissa}e$exponent"
    }
}
