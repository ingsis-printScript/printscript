package org.example.ast.expressions

import org.example.ast.visitors.ASTVisitor
import org.example.common.enums.Type

class ReadInputNode(
    val prompt: String,
    val expectedType: Type
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}
