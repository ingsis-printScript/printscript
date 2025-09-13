package org.example.interpreter.handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.common.results.Error
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableAssignerHandler : ASTNodeHandler<VariableAssigner> {
    override fun handleExecution(node: VariableAssigner, executor: Executor) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                executor.reportError("Missing expression")
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression
                val value = executor.evaluate(exprNode)

                if (!executor.isVariableDeclared(node.symbol.value)) {
                    executor.reportError("Variable ${node.symbol.value} not declared")
                    return
                }

                executor.assignVariable(node.symbol.value, value)
                executor.pushLiteral(value)
            }
        }
    }

    override fun handleValidation(node: VariableAssigner, validator: Validator) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                throw RuntimeException("Missing expression for variable ${node.symbol.value}")
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression
                val valueType = validator.evaluate(exprNode) ?: throw RuntimeException(
                    "Cannot determine type for ${node.symbol.value}"
                )

                val declaredType = validator.lookupVariable(node.symbol.value)
                if (declaredType != valueType) {
                    throw RuntimeException(
                        "Type mismatch: expected $declaredType, got $valueType"
                    )
                }
                validator.pushLiteral(valueType)
            }
        }
    }
}
