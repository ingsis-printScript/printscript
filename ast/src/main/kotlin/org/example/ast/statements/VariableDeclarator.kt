package org.example.ast.statements

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.visitor.ASTVisitor
import org.example.common.Range
import org.example.common.enums.Type

data class VariableDeclarator(
    val symbol: SymbolExpression,
    val type: Type,
    override val range: Range,
    val value: OptionalExpression
) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitVariableDeclarator(this)
    }
}
