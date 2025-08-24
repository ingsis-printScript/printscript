package org.example.common.ast.expressions

import org.example.common.Range
import org.example.common.enums.Operator

data class BinaryExpression(
    val left: Expression,
    val operator: Operator,
    val right: Expression,
    val range: Range
): Expression {
}