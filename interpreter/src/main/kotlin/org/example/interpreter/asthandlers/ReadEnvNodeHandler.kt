package org.example.interpreter.asthandlers

import org.example.ast.expressions.ReadEnvExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class ReadEnvNodeHandler : ASTNodeHandler<ReadEnvExpression> {

    override fun handleExecution(node: ReadEnvExpression, executor: Executor) {
        val rawValue = executor.getEnvVar(node.varName)

        if (rawValue == null) {
            executor.reportError("Variable '${node.varName}' not found in environment")
            return
        }

        val value = when (node.expectedType) {
            Type.NUMBER -> (rawValue as? Int) ?: run {
                executor.reportError("Expected NUMBER for '${node.varName}', but got '$rawValue'")
                return
            }
            Type.BOOLEAN -> (rawValue as? Boolean) ?: run {
                executor.reportError("Expected BOOLEAN for '${node.varName}', but got '$rawValue'")
                return
            }
            Type.STRING -> (rawValue as? String) ?: run {
                executor.reportError("Expected STRING for '${node.varName}', but got '$rawValue'")
                return
            }
        }

        executor.pushLiteral(value)
    }

    override fun handleValidation(node: ReadEnvExpression, validator: Validator) {
        validator.pushLiteral(node.expectedType)
    }
}
