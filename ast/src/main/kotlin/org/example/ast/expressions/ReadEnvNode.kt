package org.example.ast.expressions

import org.example.ast.visitors.ASTVisitor
import org.example.common.enums.Type

class ReadEnvNode(
    val varName: String,
    val expectedType: Type
) : Expression{
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}