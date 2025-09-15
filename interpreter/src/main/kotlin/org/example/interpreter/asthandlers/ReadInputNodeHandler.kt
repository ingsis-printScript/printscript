package org.example.interpreter.asthandlers

import org.example.ast.expressions.ReadInputExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class ReadInputNodeHandler : ASTNodeHandler<ReadInputExpression> {

    override fun handleExecution(node: ReadInputExpression, executor: Executor) {
        val input = executor.inputProvider.readInput(node.value)

        val value = when {
            input == null -> {
                executor.reportError("No input provided")
                null
            }
            node.expectedType == Type.NUMBER -> input.toIntOrNull().also {
                if (it == null) executor.reportError("Expected NUMBER but got '$input'")
            }
            node.expectedType == Type.BOOLEAN -> when (input.lowercase()) {
                "true" -> true
                "false" -> false
                else -> {
                    executor.reportError("Expected BOOLEAN but got '$input'")
                    null
                }
            }
            node.expectedType == Type.STRING -> input
            else -> {
                executor.reportError("Unsupported type")
                null
            }
        }

        executor.pushLiteral(value)
    }

    override fun handleValidation(node: ReadInputExpression, validator: Validator) {
        validator.pushLiteral(node.expectedType)
    }
}
