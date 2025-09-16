package org.example.ast.statements

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.visitor.ASTVisitor
import org.example.common.Range

data class VariableAssigner(
    val symbol: SymbolExpression,
    val value: OptionalExpression,
    override val range: Range
) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitVariableAssigner(this)
    }
}
