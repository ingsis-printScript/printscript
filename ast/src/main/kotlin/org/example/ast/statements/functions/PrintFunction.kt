package org.example.ast.statements.functions

import org.example.common.Range
import org.example.ast.expressions.Expression
import org.example.ast.expressions.SymbolExpression
import org.example.interpreter.visitors.ASTVisitor

data class PrintFunction(
    val symbol: SymbolExpression,
    val value: Expression,
    override val range: Range
): FunctionCall {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)

    }
}
