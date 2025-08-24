package org.example.common.ast.expressions

import org.example.common.Position
import org.example.common.tokens.TokenType


data class LiteralExpression<T>(
    val type: TokenType,
    val value: T,
    val position: Position
): Expression{
}
