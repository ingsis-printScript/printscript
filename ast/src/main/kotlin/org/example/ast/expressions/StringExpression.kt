package org.example.ast.expressions

import org.example.common.Position
import org.example.common.enums.TokenType

data class StringExpression(
    val type: TokenType,
    val value: String,
    val position: Position
) : Expression
