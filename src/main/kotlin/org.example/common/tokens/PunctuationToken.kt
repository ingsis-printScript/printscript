package org.example.common.tokens

import org.example.common.Range

data class PunctuationToken(
    val type: Punctuation,
    val range: Range
): Token