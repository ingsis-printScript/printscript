package org.example.interpreter.handlers

import org.example.ast.expressions.NumberExpression
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class NumberExpressionHandler: ASTNodeHandler<NumberExpression> {
    override fun handleExecution(
        node: NumberExpression,
        executor: Executor
    ) {
        TODO("Not yet implemented")
    }

    override fun handleValidators(
        node: NumberExpression,
        validator: Validator
    ) {
        TODO("Not yet implemented")
    }
}