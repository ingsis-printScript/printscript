package org.example.interpreter.handlers

import org.example.common.ast.expressions.IdentifierExpression
import org.example.interpreter.Executor
import org.example.interpreter.Validator


class IdentifierExpression : ASTNodeHandler<IdentifierExpression> {
    override fun handleExecution(node: IdentifierExpression, executor: Executor) {
        val value = executor.lookupVariable(node.name)
        executor.pushLiteral(value)
    }

    override fun handleValidators(node: IdentifierExpression, validator: Validator) {
        TODO("Not yet implemented")
    }
}