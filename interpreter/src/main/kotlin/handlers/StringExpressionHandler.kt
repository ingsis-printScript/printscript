package org.example.interpreter.handlers

import org.example.ast.expressions.StringExpression
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class StringExpressionHandler: ASTNodeHandler<StringExpression> {

    override fun handleExecution(
        node: StringExpression,
        executor: Executor
    ) {
        TODO("Not yet implemented")
    }

    override fun handleValidators(
        node: StringExpression,
        validator: Validator
    ) {
        TODO("Not yet implemented")
    }
}