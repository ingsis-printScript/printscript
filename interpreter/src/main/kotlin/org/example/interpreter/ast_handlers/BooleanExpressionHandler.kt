package org.example.interpreter.handlers

import org.example.ast.expressions.BooleanExpression
import org.example.common.enums.Type
import org.example.interpreter.org.example.interpreter.Executor
import org.example.interpreter.org.example.interpreter.Validator

class BooleanExpressionHandler : ASTNodeHandler<BooleanExpression> {

    override fun handleExecution(node: BooleanExpression, executor: Executor) {
        val value = when (node.value.lowercase()) {
            "true" -> true
            "false" -> false
            else -> {
                executor.reportError("Invalid boolean value: ${node.value}")
                return
            }
        }

        executor.pushLiteral(value)
    }

    override fun handleValidation(node: BooleanExpression, validator: Validator) {
        validator.pushLiteral(Type.BOOLEAN)
    }
}
