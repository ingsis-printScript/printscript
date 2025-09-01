package org.example.ast

import org.example.interpreter.visitors.ASTVisitor

interface ASTNode{
    fun <T> accept(visitor: ASTVisitor<T>): T
}