package org.example.interpreter.handlers

import org.example.common.ast.expressions.LiteralExpression
import org.example.interpreter.Executor
import org.example.interpreter.Validator


class LiteralHandler : ASTNodeHandler<LiteralExpression<*>> {
    override fun handleExecution(node: LiteralExpression<*>, executor: Executor) {
        // Handle the execution of the literal expression
        executor.pushLiteral(node.value)
        }

    override fun handleValidators(node: LiteralExpression<*>, validator: Validator) {
        TODO("Not yet implemented")
    }
}