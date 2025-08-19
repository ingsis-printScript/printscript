package org.example.common.ast.expressions

import org.example.common.Position
import org.example.common.enums.Operator

class BinaryExpression(
    val left: Expression,
    val operator: Operator,
    val right: Expression,
    val position: Position
): Expression {
}