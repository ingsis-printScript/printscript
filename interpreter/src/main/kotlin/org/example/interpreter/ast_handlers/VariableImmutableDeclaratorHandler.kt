package org.example.interpreter.handlers

import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.interpreter.org.example.interpreter.Executor
import org.example.interpreter.org.example.interpreter.Validator

class VariableImmutableDeclaratorHandler : ASTNodeHandler<VariableImmutableDeclarator> {

    override fun handleExecution(node: VariableImmutableDeclarator, executor: Executor) {
        val value = when (val opt = node.value) {
            is OptionalExpression.NoExpression -> null
            is OptionalExpression.HasExpression -> executor.evaluate(opt.expression)
        }

        executor.declareVariable(node.symbol.value, value)
    }

    override fun handleValidation(node: VariableImmutableDeclarator, validator: Validator) {
        validator.declareVariable(node.symbol.value, node.type)

        when (val opt = node.value) {
            is OptionalExpression.NoExpression -> {
                validator.pushLiteral(node.type)
            }
            is OptionalExpression.HasExpression -> {
                val valueType = validator.evaluate(opt.expression)
                if (valueType == null) {
                    validator.reportError("Cannot determine type for '${node.symbol.value}'")
                } else if (valueType != node.type) {
                    validator.reportError("Type mismatch for '${node.symbol.value}': expected ${node.type}, got $valueType")
                } else {
                    validator.pushLiteral(node.type)
                }
            }
        }
    }
}
