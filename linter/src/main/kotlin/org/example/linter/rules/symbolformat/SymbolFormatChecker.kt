package org.example.linter.rules.symbolformat

import org.example.common.Range

interface SymbolFormatChecker {
    fun isValid(symbol: String): Boolean
    fun message(symbol: String, range: Range): String
}
