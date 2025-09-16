package org.example.ast.expressions

import org.example.ast.ASTNode
import org.example.ast.visitor.ASTVisitor

sealed interface Expression : ASTNode {
    override fun <T> accept(visitor: ASTVisitor<T>): T
}
