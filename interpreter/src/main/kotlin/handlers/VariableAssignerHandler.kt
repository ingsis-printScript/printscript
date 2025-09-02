package org.example.interpreter.handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.interpreter.Executor
import org.example.interpreter.Validator

class VariableAssignerHandler : ASTNodeHandler<VariableAssigner> {
    override fun handleExecution(node: VariableAssigner, executor: Executor) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                throw RuntimeException("Missing expression")
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression  // ✅ exprNode es ahora el Expression real
                val value = executor.evaluate(exprNode)

                if (!executor.isVariableDeclared(node.symbol.value)) {
                    throw RuntimeException("Variable ${node.symbol.value} not declared")
                }

                executor.assignVariable(node.symbol.value, value)
            }
        }
    }


    override fun handleValidators(node: VariableAssigner, validator: Validator) {
        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                throw RuntimeException("Missing expression")
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression
                val valueType = validator.evaluate(exprNode)

                if (valueType != null) {
                    val declaredType = validator.lookupVariable(node.symbol.value)
                    if (declaredType != valueType) {
                        throw RuntimeException(
                            "Type mismatch: expected $declaredType, got $valueType"
                        )
                    }
                } else {
                    throw RuntimeException("Cannot determine type for ${node.symbol.value}")
                }
            }
        }
    }



}
