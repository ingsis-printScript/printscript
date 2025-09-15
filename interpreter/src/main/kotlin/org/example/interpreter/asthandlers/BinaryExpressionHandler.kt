package org.example.interpreter.asthandlers

import org.example.ast.expressions.BinaryExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.interpreter.Executor
import org.example.interpreter.Validator
import org.example.interpreter.handlers.ASTNodeHandler

class BinaryExpressionHandler : ASTNodeHandler<BinaryExpression> {
    override fun handleExecution(node: BinaryExpression, executor: Executor) {
        val left = executor.evaluate(node.left) as Number
        val right = executor.evaluate(node.right) as Number

        val result = when (node.operator) {
            Operator.ADD -> left.toDouble() + right.toDouble()
            Operator.SUB -> left.toDouble() - right.toDouble()
            Operator.MUL -> left.toDouble() * right.toDouble()
            Operator.DIV -> left.toDouble() / right.toDouble()
            Operator.MOD -> left.toDouble() % right.toDouble()
            else -> throw IllegalArgumentException("Unsupported operator: ${node.operator}")
        }

        executor.pushLiteral(collapseNumber(result))
    }

    override fun handleValidation(node: BinaryExpression, validator: Validator) {
        val leftType = validator.evaluate(node.left)
        val rightType = validator.evaluate(node.right)

        if (!isNumericType(leftType) || !isNumericType(rightType)) {
            throw IllegalStateException(
                "Binary operation ${node.operator} requires numeric operands, got $leftType and $rightType at ${node.range}"
            )
        }

        validator.pushLiteral(Type.NUMBER)
    }

    private fun isNumericType(type: Type?) = type == Type.NUMBER

    private fun collapseNumber(value: Double): Number {
        return if (value % 1.0 == 0.0) {
            value.toInt()
        } else {
            value
        }
    }
}
