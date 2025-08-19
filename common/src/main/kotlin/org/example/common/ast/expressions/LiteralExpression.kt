package org.example.common.ast.expressions

import org.example.common.Position
import org.example.common.tokens.TokenType


class LiteralExpression<T>(
    val type: TokenType,
    val value: T,
    override val position: Position
): Expression{
}