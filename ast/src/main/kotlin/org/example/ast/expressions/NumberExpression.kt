package org.example.ast.expressions

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.interpreter.visitors.ASTVisitor

data class NumberExpression(
    val type: TokenType,
    val value: String,
    val position: Position
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}