package org.example.linter.rules.symbolformat

import org.example.common.Range

class CamelCaseChecker : SymbolFormatChecker {
    override fun isValid(symbol: String): Boolean =
        symbol.isNotEmpty()
                && symbol[0].isLowerCase()
                && !symbol.contains('_')
                && symbol.all { it.isLetterOrDigit() }

    override fun message(symbol: String, range: Range): String =
        "Symbol '$symbol' at $range should be in camelCase format"
}