package org.example.interpreter.asthandlers

import org.example.ast.expressions.NumberExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class NumberExpressionHandler : ASTNodeHandler<NumberExpression> {

    override fun handleExecution(node: NumberExpression, executor: Executor) {
        val value = parseNumber(node.value)
        executor.pushLiteral(value)
    }

    override fun handleValidation(node: NumberExpression, validator: Validator) {
        validator.pushLiteral(Type.NUMBER)
    }

    fun parseNumber(s: String): Number {
        return if (s.contains('.') || s.contains(',')) {
            s.toDouble()
        } else {
            s.toInt()
        }
    }
}
