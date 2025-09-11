package org.example.ast.expressions

import org.example.ast.visitors.ASTVisitor
import org.example.common.Position

data class StringExpression(
    val value: String,
    val position: Position
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}
