package org.example.common.ast.expressions

import org.example.common.Range
import org.example.common.tokens.enums.Operator

class BinaryExpression(
    val left: Expression,
    val operator: Operator,
    val right: Expression,
    override val range: Range
): Expression {
}