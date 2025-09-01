package org.example.interpreter.handlers

import org.example.ast.expressions.SymbolExpression
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class SymbolExpressionHandler: ASTNodeHandler<SymbolExpression> {
    override fun handleExecution(
        node: SymbolExpression,
        executor: Executor
    ) {
        TODO("Not yet implemented")
    }

    override fun handleValidators(
        node: SymbolExpression,
        validator: Validator
    ) {
        TODO("Not yet implemented")
    }
}