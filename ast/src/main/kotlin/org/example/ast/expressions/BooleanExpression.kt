package org.example.ast.expressions

import org.example.common.Position

data class BooleanExpression(
    val value: String,
    val position: Position
) : Expression {

}
