package org.example.common.ast.expressions

import org.example.common.Position
import org.example.common.enums.Operator

data class BinaryExpression(
    val left: Expression,
    val operator: Operator,
    val right: Expression,
    override val position: Position
): Expression {
}