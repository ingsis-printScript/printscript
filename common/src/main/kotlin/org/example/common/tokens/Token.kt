package org.example.common.tokens

import org.example.common.Position

data class Token(
    val type: TokenType,
    val value: String,
    val position: Position,
)

