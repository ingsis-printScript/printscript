package org.example.interpreter.asthandlers

import org.example.ast.expressions.NumberExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class NumberExpressionHandler : ASTNodeHandler<NumberExpression> {

    override fun handleExecution(node: NumberExpression, executor: Executor) {
        val value = node.value.toDouble()
        executor.pushLiteral(value)
    }

    override fun handleValidation(node: NumberExpression, validator: Validator) {
        validator.pushLiteral(Type.NUMBER)
    }
}
