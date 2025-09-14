package org.example.ast.expressions

import org.example.common.Position

data class StringExpression(
    val value: String,
    val position: Position
) : Expression {
}
