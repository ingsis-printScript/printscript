package org.example.linter.rules.symbolformat.checker

import org.example.common.Range

class SnakeCaseChecker : SymbolFormatChecker {
    override fun isValid(symbol: String): Boolean =
        symbol.isNotEmpty() &&
            symbol == symbol.lowercase() &&
            symbol.all { it.isLetterOrDigit() || it == '_' } &&
            !symbol.endsWith('_') &&
            !symbol.contains("__")

    override fun message(symbol: String, range: Range): String =
        "Symbol '$symbol' at $range should be in snake_case format"
}
