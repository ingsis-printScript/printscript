package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.ValidationResult

class ExpressionValidator : TokenValidator {

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        var pos = position
        var expectingElement = true
        val parenStack = ArrayDeque<Token>()

        while (pos < statement.size) {
            val token = statement[pos]

            if (isSemicolon(token)) break

            if (expectingElement) {
                when {
                    TokenType.isElement(token.type) -> {
                        expectingElement = false
                        pos++
                    }
                    isStartingParen(token) -> {
                        parenStack.addLast(token)
                        pos++
                    }
                    else -> return error(pos, "Expected element or '(', found '${token.value}'")
                }
            } else {
                when {
                    isOperator(token) -> {
                        expectingElement = true
                        pos++
                    }
                    isClosingParen(token) -> {
                        if (parenStack.isEmpty()) return error(pos, "Unmatched closing parenthesis ')'")
                        parenStack.removeLast()
                        pos++
                    }
                    else -> return error(pos, "Expected operator or ')', found '${token.value}'")
                }
            }
        }

        if (parenStack.isNotEmpty()) return error(pos, "Unmatched opening parenthesis '('")

        if (expectingElement && pos > position) return error(pos, "Expression cannot end with an operator")

        return ValidationResult.Success(pos - position)
    }

    private fun isSemicolon(token: Token) = isPunctuation(token) && token.value == ";"

    private fun error(pos: Int, message: String): ValidationResult.Error {
        return ValidationResult.Error(message, pos)
    }

    private fun isOperator(token: Token) = token.type == TokenType.OPERATOR

    private fun isClosingParen(token: Token) = isPunctuation(token) && token.value == ")"

    private fun isPunctuation(token: Token) = token.type == TokenType.PUNCTUATION

    private fun isStartingParen(token: Token) = isPunctuation(token) && token.value == "("

    override fun getExpectedDescription(): String {
        return "An expression with elements, operators, parentheses ending with ';'"
    }
}
