package org.example.ast.expressions

import org.example.ast.visitor.ASTVisitor
import org.example.common.Range

class ReadEnvExpression(
    val value: OptionalExpression,
    val range: Range
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitReadEnv(this)
    }
}
