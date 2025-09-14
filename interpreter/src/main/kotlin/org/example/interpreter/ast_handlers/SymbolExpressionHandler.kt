package org.example.interpreter.ast_handlers

import org.example.ast.expressions.SymbolExpression
import org.example.interpreter.Executor
import org.example.interpreter.handlers.ASTNodeHandler
import org.example.interpreter.Validator

class SymbolExpressionHandler : ASTNodeHandler<SymbolExpression> {

    override fun handleExecution(node: SymbolExpression, executor: Executor) {
        val value = executor.lookupVariable(node.value)

        if (value == null) {
            executor.reportError("Undefined symbol: ${node.value}")
            executor.pushLiteral(null)
        } else {
            executor.pushLiteral(value)
        }
    }


    override fun handleValidation(node: SymbolExpression, validator: Validator) {
        val type = validator.lookupSymbol(node.value)

        if (type != null) {
            validator.pushLiteral(type)
        }
    }
}
