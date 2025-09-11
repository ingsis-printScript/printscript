package org.example.interpreter.handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableDeclarator
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableDeclaratorHandler : ASTNodeHandler<VariableDeclarator> {

    override fun handleExecution(node: VariableDeclarator, executor: Executor) {
        val value = when (val opt = node.value) {
            is OptionalExpression.NoExpression -> null
            is OptionalExpression.HasExpression -> executor.evaluate(opt.expression)
        }
        executor.declareVariable(node.symbol.value, node.type, value)
        executor.returnResult(Success(value))
    }

    override fun handleValidators(node: VariableDeclarator, validator: Validator) {
        validator.declareVariable(node.symbol.value, node.type)
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> return validator.returnResult(Success(node.type))
            is OptionalExpression.HasExpression -> {
                val type = validator.evaluate(opt.expression)
                if (type == null) return validator.returnResult(Error("Cannot determine type for '${node.symbol.value}'"))
                if (type != node.type) return validator.returnResult(Error("Type mismatch for '${node.symbol.value}': expected ${node.type}, got $type"))
                return validator.returnResult(Success(node.type))
            }
        }
    }
}
