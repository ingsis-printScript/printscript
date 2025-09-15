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

        when (rawValue) {
            is Double, is Boolean, is String -> executor.pushLiteral(rawValue)
            else -> executor.reportError(
                "Variable '${node.varName}' tiene un tipo no soportado: ${rawValue::class.simpleName}"
            )
        }
    }

    override fun handleValidation(node: ReadEnvExpression, validator: Validator) {
        val type = validator.lookupSymbol(node.varName)
        if (type == null) {
            validator.reportError("Variable '${node.varName}' no declarada")
            return
        }
        validator.pushLiteral(type)
    }
}

