package org.example.interpreter.handlers

import org.example.ast.expressions.BinaryExpression
import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.interpreter.org.example.interpreter.Executor
import org.example.interpreter.org.example.interpreter.Validator

class BinaryExpressionHandler : ASTNodeHandler<BinaryExpression> {
    override fun handleExecution(node: BinaryExpression, executor: Executor) {
        val left = executor.evaluate(node.left) as Number
        val right = executor.evaluate(node.right) as Number

        val result = when (node.operator) {
            Operator.ADD -> left.toInt() + right.toInt()
            Operator.SUB -> left.toInt() - right.toInt()
            Operator.MUL -> left.toInt() * right.toInt()
            Operator.DIV -> left.toInt() / right.toInt()
            Operator.MOD -> left.toInt() % right.toInt()
            else -> throw IllegalArgumentException("Unsupported operator: ${node.operator}")
        }

        executor.pushLiteral(result)
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
}
