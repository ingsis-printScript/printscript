package org.example.parser.parsers.expressionbuilder

import org.example.ast.expressions.BinaryExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Operator
import org.example.common.enums.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.expressionbuilder.rules.OperatorPrecedence
import org.example.parser.parsers.expressionbuilder.rules.ParenthesesHandler
import org.example.token.Token

class ExpressionBuilder {

    private val operatorPrecedence = OperatorPrecedence()
    private val parenthesesHandler = ParenthesesHandler()
    private val expressionFactory = ExpressionFactory()

    fun buildExpression(tokens: List<Token>, start: Int, end: Int): OptionalExpression {
        if (hasNoExpression(start, end)) {
            return OptionalExpression.NoExpression
        }
        val expression = buildWithPrecedence(tokens, start, end, 0)
        return OptionalExpression.HasExpression(expression)
    }

    private fun buildWithPrecedence(tokens: List<Token>, start: Int, end: Int, minPrecedence: Int): Expression {
        var left = parsePrimaryExpression(tokens, start, end)
        var pos = skipPrimaryExpression(tokens, start, end)

        while (pos < end && tokens[pos].type == TokenType.OPERATOR) {
            val operatorToken = tokens[pos]
            val operator = Operator.fromString(operatorToken.value)
                ?: throw SyntaxException("Invalid operator: ${operatorToken.value}")

            val precedence = operatorPrecedence.getPrecedence(operator)
            if (precedence < minPrecedence) break

            val right = buildWithPrecedence(tokens, pos + 1, end, precedence + 1)
            val range = createRange(tokens[start], tokens[end - 1])
            left = BinaryExpression(left, operator, right, range)

            pos = findNextOperator(tokens, pos + 1, end)
        }

        return left
    }

    private fun parsePrimaryExpression(tokens: List<Token>, start: Int, end: Int): Expression {
        if (start >= end) {
            throw SyntaxException("Expected expression")
        }

        val token = tokens[start]

        if (parenthesesHandler.isOpenParen(token)) {
            val closeParenIndex = parenthesesHandler.findMatchingCloseParen(tokens, start)
            return buildExpression(tokens, start + 1, closeParenIndex) as Expression
        }

        return expressionFactory.createExpression(token)
    }

    private fun skipPrimaryExpression(tokens: List<Token>, start: Int, end: Int): Int {
        if (start >= end) return end

        val token = tokens[start]

        if (parenthesesHandler.isOpenParen(token)) {
            return parenthesesHandler.findMatchingCloseParen(tokens, start) + 1
        }

        return start + 1
    }

    private fun findNextOperator(tokens: List<Token>, start: Int, end: Int): Int {
        var pos = start
        var depth = 0

        while (pos < end) {
            val token = tokens[pos]
            when {
                parenthesesHandler.isOpenParen(token) -> depth++
                parenthesesHandler.isCloseParen(token) -> depth--
                token.type == TokenType.OPERATOR && depth == 0 -> return pos
            }
            pos++
        }
        return end
    }

    private fun createRange(startToken: Token, endToken: Token): Range {
        return Range(
            Position(startToken.position.line, startToken.position.column),
            Position(endToken.position.line, endToken.position.column)
        )
    }

    private fun hasNoExpression(start: Int, end: Int): Boolean {
        return start >= end
    }
}