package org.example.ast.expressions

import org.example.common.Position
import org.example.common.enums.TokenType

data class NumberExpression(
    val type: TokenType,
    val value: String,
    val position: Position
) : Expression