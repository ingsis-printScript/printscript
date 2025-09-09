package org.example.parser.expressionbuilder

import org.example.ast.expressions.BooleanExpression
import org.example.ast.expressions.Expression
import org.example.ast.expressions.NumberExpression
import org.example.ast.expressions.StringExpression
import org.example.ast.expressions.SymbolExpression
import org.example.common.enums.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.token.Token

class ExpressionFactory {
    fun createExpression(token: Token): Expression {
        return when (token.type) {
            TokenType.NUMBER -> NumberExpression(token.value, token.position)
            TokenType.STRING -> StringExpression(token.value, token.position)
            TokenType.SYMBOL -> SymbolExpression(token.value, token.position)
            TokenType.BOOLEAN -> BooleanExpression(token.value, token.position)
            else -> throw SyntaxException("Unexpected token type: ${token.type}")
        }
    }
}