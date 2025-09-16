package org.example.ast

import org.example.ast.visitor.ASTVisitor

interface ASTNode {
    fun <T> accept(visitor: ASTVisitor<T>): T
}
