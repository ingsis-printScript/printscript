package org.example.ast.statements.functions

import org.example.ast.statements.Statement
import org.example.ast.visitor.ASTVisitor

interface FunctionCall : Statement{
    fun <T> accept(visitor: ASTVisitor<T>): T
}
