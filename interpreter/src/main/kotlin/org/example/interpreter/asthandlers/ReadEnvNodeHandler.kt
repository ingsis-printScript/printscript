package org.example.interpreter.asthandlers

import org.example.ast.expressions.ReadEnvExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class ReadEnvNodeHandler : ASTNodeHandler<ReadEnvExpression> {

    override fun handleExecution(node: ReadEnvExpression, executor: Executor) {
        val rawValue = System.getenv(node.varName)

        if (rawValue == null) {
            executor.reportError("Environment variable '${node.varName}' not found")
            return
        }

        val value = when (node.expectedType) {
            Type.NUMBER -> rawValue.toIntOrNull() ?: run {
                executor.reportError("Expected NUMBER for '${node.varName}', but got '$rawValue'")
                return
            }
            Type.BOOLEAN -> when (rawValue.lowercase()) {
                "true" -> true
                "false" -> false
                else -> {
                    executor.reportError("Expected BOOLEAN for '${node.varName}', but got '$rawValue'")
                    return
                }
            }
            Type.STRING -> rawValue
        }

        executor.pushLiteral(value)
    }

    override fun handleValidation(node: ReadEnvExpression, validator: Validator) {
        validator.pushLiteral(node.expectedType)
    }
}
