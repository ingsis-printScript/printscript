package org.example.ast.expressions

import org.example.ast.visitor.ASTVisitor
import org.example.common.Position

data class BooleanExpression(
    val value: String,
    val position: Position
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return  visitor.visitBoolean(this)  }
}
