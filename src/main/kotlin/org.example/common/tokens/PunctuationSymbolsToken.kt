package org.example.common.tokens

import org.example.common.Range
import org.example.common.tokens.enums.Punctuation

data class PunctuationSymbolsToken(
    val type: Punctuation,
    val range: Range
): Token