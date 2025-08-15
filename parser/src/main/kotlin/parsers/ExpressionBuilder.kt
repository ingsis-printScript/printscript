package parsers

import org.example.common.Range
import org.example.common.ast.expressions.BinaryExpression
import org.example.common.ast.expressions.Expression
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.expressions.LiteralExpression
import org.example.common.enums.Operator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType

class ExpressionBuilder {

    fun buildExpression(tokens: List<Token>, start: Int, end: Int): Expression { //tengo que pasarle 0, tokens.size
        if (end - start == 1) {
            val token = tokens[start]
            return when {
                token.type == TokenType.NUMBER || token.type == TokenType.STRING ->
                    LiteralExpression(token.type, token.value, token.range)

                token.type == TokenType.SYMBOL ->
                    IdentifierExpression(token.value, token.range)

                else ->
                    throw IllegalArgumentException("Unexpected token: ${token.value}")
            }


        }

        for (i in start until end) {
            val token = tokens[i]
            if (token.type == TokenType.OPERATOR) {
                val operator = Operator.fromString(token.value)
                if (operator == null) {
                    throw IllegalArgumentException("Invalid operator: ${token.value}")
                }

                val leftExpr = buildExpression(tokens, start, i)
                val rightExpr = buildExpression(tokens, i + 1, end)

                val range = Range(
                    tokens[start].range.start,
                    tokens[end - 1].range.end
                )

                return BinaryExpression(leftExpr, operator, rightExpr, range)
            }
        }

        throw IllegalArgumentException("Invalid expression: ${tokens.subList(start, end)}")
    }

}