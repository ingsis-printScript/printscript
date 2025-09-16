package org.example.ast.expressions

import org.example.ast.visitor.ASTVisitor
import org.example.common.Range
import org.example.common.enums.Operator

data class BinaryExpression(
    val left: Expression,
    val operator: Operator,
    val right: Expression,
    val range: Range
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitBinary(this)
    }
}
