package org.example.interpreter.handlers

import org.example.common.ast.expressions.BinaryExpression
import org.example.common.enums.Operator
import org.example.interpreter.Executor
import org.example.interpreter.Validator


class BinaryExpressionHandler : ASTNodeHandler<BinaryExpression> {
    override fun handleExecution(node: BinaryExpression, executor: Executor) {
        val left = executor.evaluate(node.left)
        val right = executor.evaluate(node.right)
        val result = when (node.operator) {
            Operator.ADD -> (left as Int) + (right as Int)
            Operator.SUB -> (left as Int) - (right as Int)
            Operator.MUL -> (left as Int) * (right as Int)
            Operator.DIV -> (left as Int) / (right as Int)
            else -> throw IllegalArgumentException("Unsupported operator: ${node.operator}")
        }
        executor.pushLiteral(result)
    }

    override fun handleValidators(node: BinaryExpression, validator: Validator) {
        TODO("Not yet implemented")
    }
}