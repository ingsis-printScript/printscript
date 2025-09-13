package org.example.interpreter.handlers

import org.example.ast.expressions.NumberExpression
import org.example.common.enums.Type
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class NumberExpressionHandler : ASTNodeHandler<NumberExpression> {
    override fun handleExecution(node: NumberExpression, executor: Executor) {
        executor.pushLiteral(node.value)
        executor.returnResult(Success(node.value))
    }

    override fun handleValidation(
        node: NumberExpression,
        validator: Validator
    ) {
        validator.pushLiteral(Type.NUMBER)
        validator.returnResult(Success("number"))
    }
}
