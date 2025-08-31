package org.example.ast

import org.example.interpreter.visitors.ASTVisitor

interface ASTNode{
    fun accept(visitor: ASTVisitor)
}