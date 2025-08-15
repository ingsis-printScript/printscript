package org.example.common.tokens

import org.example.common.Range

data class Token(
    val type: TokenType,
    val value: String,
    val range: Range,
)

