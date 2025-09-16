package org.example.ast.statements

import org.example.ast.ASTNode
import org.example.ast.visitor.ASTVisitor
import org.example.common.Range

interface Statement : ASTNode {
    val range: Range

    override fun <T> accept(visitor: ASTVisitor<T>): T
}
