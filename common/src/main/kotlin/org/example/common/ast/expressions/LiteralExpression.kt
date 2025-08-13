package org.example.common.ast.expressions

import org.example.common.Range


class LiteralExpression<T>(
    val type: String,
    val value: T, //Es String o Number/int,
    override val range: Range
): Expression{
}