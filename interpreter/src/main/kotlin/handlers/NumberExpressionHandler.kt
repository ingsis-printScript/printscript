package org.example.interpreter.handlers

import org.example.ast.expressions.NumberExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.result.Success

class NumberExpressionHandler: ASTNodeHandler<NumberExpression> {
    override fun handleExecution(node: NumberExpression, executor: Executor) {
        executor.pushLiteral(node.value)
        executor.returnResult(Success(node.value))
    }


    override fun handleValidators(
        node: NumberExpression,
        validator: Validator
    ) {
        validator.pushLiteral(Type.NUMBER)
        validator.returnResult(Success("number"))
    }
}