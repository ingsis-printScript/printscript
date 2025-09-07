package org.example.interpreter.handlers

import org.example.ast.expressions.BinaryExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.results.Success
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.result.Success

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
        executor.returnResult(Success(result))
    }

    override fun handleValidators(node: BinaryExpression, validator: Validator) {
        val leftType = validator.evaluate(node.left)
        val rightType = validator.evaluate(node.right)

        // Ambos operandos deben ser enteros para operaciones aritméticas
        if (leftType != Type.NUMBER || rightType != Type.NUMBER) {
            throw IllegalStateException(
                "Binary operation ${node.operator} requires INT operands, got $leftType and $rightType at ${node.range}"
            )
        }

        // El resultado de una operación aritmética es INT
        validator.pushLiteral(Type.NUMBER)
        validator.returnResult(Success(Type.NUMBER))
    }


}
