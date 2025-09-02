package org.example.ast.statements.functions

import org.example.common.Range
import org.example.ast.expressions.OptionalExpression
import org.example.ast.visitors.ASTVisitor

data class PrintFunction(
    val value: OptionalExpression,
    override val range: Range
): FunctionCall {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)

    }
}
