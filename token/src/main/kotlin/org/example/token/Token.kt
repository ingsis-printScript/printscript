package org.example.token

import org.example.common.Position
import org.example.common.enums.TokenType

data class Token(
    val type: TokenType,
    val value: String,
    val position: Position
)
