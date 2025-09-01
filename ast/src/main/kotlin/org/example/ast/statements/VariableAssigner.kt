package org.example.ast.statements

import org.example.common.Range
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.interpreter.visitors.ASTVisitor

data class VariableAssigner(
    val symbol: SymbolExpression,
    val value: OptionalExpression,
    override val range: Range
) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)

    }
}
