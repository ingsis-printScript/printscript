package org.example.common.ast.expressions

import org.example.common.Range
import org.example.common.tokens.TokenType


class LiteralExpression<T>(
    val type: TokenType,
    val value: T,
    override val range: Range
): Expression{
}