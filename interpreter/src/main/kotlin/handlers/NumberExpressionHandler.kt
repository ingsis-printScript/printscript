package org.example.interpreter.handlers

import org.example.ast.expressions.NumberExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.result.Success

class NumberExpressionHandler: ASTNodeHandler<NumberExpression> {
    override fun handleExecution(node: NumberExpression, executor: Executor) {
        executor.pushLiteral(node.value) // node.value is the number
        executor.returnResult(Success(node.value)) // wrap it in Success
    }


    override fun handleValidators(
        node: NumberExpression,
        validator: Validator
    ) {
        validator.pushLiteral(Type.NUMBER) // or "Double", depending on your NumberExpression
        validator.returnResult(Success("Int"))
    }
}