package org.example.interpreter.handlers

import org.example.ast.expressions.StringExpression
import org.example.common.enums.Type
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class StringExpressionHandler : ASTNodeHandler<StringExpression> {

    override fun handleExecution(
        node: StringExpression,
        executor: Executor
    ) {
        executor.pushLiteral(node.value)
        executor.returnResult(Success(node.value))
    }

    override fun handleValidators(
        node: StringExpression,
        validator: Validator
    ) {
        validator.pushLiteral(Type.STRING)
        validator.returnResult(Success("String"))
    }
}
