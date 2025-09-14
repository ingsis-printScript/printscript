package org.example.interpreter.asthandlers

import org.example.ast.expressions.BooleanExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

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
