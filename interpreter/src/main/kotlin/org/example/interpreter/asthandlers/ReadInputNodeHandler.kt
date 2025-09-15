package org.example.interpreter.asthandlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class ReadInputNodeHandler : ASTNodeHandler<ReadInputExpression> {

    override fun handleExecution(node: ReadInputExpression, executor: Executor) {
        val prompt: String = when (val opt = node.value) {
            is OptionalExpression.HasExpression -> executor.evaluate(opt.expression) as? String ?: ""
            is OptionalExpression.NoExpression -> ""
        }

        val input = executor.inputProvider.readInput(prompt)

        if (input == null) {
            executor.reportError("No input provided")
            return
        }

        val value: Any
        val typeDetected: String = when {
            input.equals("true", ignoreCase = true) || input.equals("false", ignoreCase = true) -> {
                value = input.equals("true", ignoreCase = true)
                "BOOLEAN"
            }
            input.toIntOrNull() != null -> {
                value = input.toInt()
                "NUMBER"
            }
            input.toDoubleOrNull() != null -> {
                value = input.toDouble()
                "NUMBER"
            }
            else -> {
                value = input
                "STRING"
            }
        }

        if (typeDetected == "STRING" || typeDetected == "NUMBER" || typeDetected == "BOOLEAN") {
            executor.pushLiteral(value)
        } else {
            executor.reportError("Invalid input type: '$input'")
        }
    }

    override fun handleValidation(node: ReadInputExpression, validator: Validator) {
        validator.pushLiteral(null)

    }
}

