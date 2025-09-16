package org.example.ast.expressions

import org.example.ast.visitor.ASTVisitor
import org.example.common.Range

class ReadInputExpression(
    val value: OptionalExpression,
    val range: Range
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitReadInput(this)
    }
}
