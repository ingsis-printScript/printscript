package org.example.ast.expressions

import org.example.ast.visitor.ASTVisitor
import org.example.common.Range
import org.example.common.enums.Type

class ReadEnvExpression(
    val varName: String,
    val range: Range
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitReadEnv(this)
    }
}
