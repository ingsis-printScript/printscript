package org.example.common.tokens

import org.example.common.Range

data class LiteralToken<T>(
    val type: String,
    val raw: String,
    val value: T,
    val range: Range
): Token
