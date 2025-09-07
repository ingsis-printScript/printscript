package org.example.interpreter.handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.common.results.Success
import org.example.common.results.Error
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableAssignerHandler : ASTNodeHandler<VariableAssigner> {
    override fun handleExecution(node: VariableAssigner, executor: Executor) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                executor.returnResult(Error("Missing expression"))
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression
                val value = executor.evaluate(exprNode)

                if (!executor.isVariableDeclared(node.symbol.value)) {
                    executor.returnResult(Error("Variable ${node.symbol.value} not declared"))
                    return
                }

                executor.assignVariable(node.symbol.value, value)
                executor.returnResult(Success(value))
            }
        }
    }

    override fun handleValidators(node: VariableAssigner, validator: Validator) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                validator.returnResult(Error("Missing expression"))
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression
                val valueType = validator.evaluate(exprNode)

                if (valueType == null) {
                    validator.returnResult(Error("Cannot determine type for ${node.symbol.value}"))
                    return
                }

                val declaredType = validator.lookupVariable(node.symbol.value)
                if (declaredType != valueType) {
                    validator.returnResult(
                        Error("Type mismatch: expected $declaredType, got $valueType")
                    )
                } else {
                    validator.returnResult(Success(valueType))
                }
            }
        }
    }
}

