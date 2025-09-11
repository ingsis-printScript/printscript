package org.example.interpreter.handlers

import org.example.ast.expressions.SymbolExpression
import org.example.common.enums.Type
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class SymbolExpressionHandler : ASTNodeHandler<SymbolExpression> {
    override fun handleExecution(node: SymbolExpression, executor: Executor) {
        val value = executor.lookupVariable(node.value)

        if (value == null) {
            executor.returnResult(Error("Undefined symbol: ${node.value}"))
        } else {
            executor.pushLiteral(value.toString())
            executor.returnResult(Success(value))
        }
    }

    override fun handleValidators(node: SymbolExpression, validator: Validator) {
        val type = validator.lookupSymbol(node.value)

        if (type == null) {
            validator.returnResult(Error("Undefined symbol: ${node.value}"))
        } else {
            validator.pushLiteral(Type.STRING)
            validator.returnResult(Success(type))
        }
    }
}
