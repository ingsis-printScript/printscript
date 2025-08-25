package org.example.interpreter.handlers

import org.example.common.ast.ASTNode
import org.example.interpreter.Executor

interface ASTNodeHandler<T: ASTNode> {
    fun handleExecution(node: T, executor: Executor)
}