package org.example.interpreter.asthandlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableAssigner
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

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
                validator.reportError("Missing expression for variable ${node.symbol.value}")
                return
            }
            is OptionalExpression.HasExpression -> {
                val exprNode = opt.expression
                val valueType = validator.evaluate(exprNode)

                if (valueType == null) {
                    validator.reportError("Cannot determine type for ${node.symbol.value}")
                    return
                }

                val declaredType = validator.lookupVariable(node.symbol.value)
                if (declaredType == null) {
                    validator.reportError("Variable ${node.symbol.value} not declared")
                    return
                }
                if (declaredType != valueType) {
                    validator.reportError(
                        "Type mismatch: expected $declaredType, got $valueType"
                    )
                    return
                }
                validator.pushLiteral(valueType)
            }
        }
    }
}
