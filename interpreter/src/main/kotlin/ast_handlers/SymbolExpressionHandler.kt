package org.example.interpreter.handlers

import org.example.ast.expressions.SymbolExpression
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class SymbolExpressionHandler : ASTNodeHandler<SymbolExpression> {

    override fun handleExecution(node: SymbolExpression, executor: Executor) {
        val value = executor.lookupVariable(node.value)

        if (value == null) {
            executor.reportError("Undefined symbol: ${node.value}")
        } else {
            executor.pushLiteral(value)
        }
    }

    override fun handleValidation(node: SymbolExpression, validator: Validator) {
        val type = validator.lookupSymbol(node.value)

        if (type == null) {
            validator.reportError("Undefined symbol: ${node.value}")
        } else {
            validator.pushLiteral(type)
        }
    }
}
