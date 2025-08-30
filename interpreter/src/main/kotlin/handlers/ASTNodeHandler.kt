package org.example.interpreter.handlers

import org.example.ast.ASTNode
import org.example.interpreter.Executor
import org.example.interpreter.Validator

interface ASTNodeHandler<T : ASTNode> {
    fun handleExecution(node: T, executor: Executor)
    fun handleValidators(node: T, validator: Validator)
}
