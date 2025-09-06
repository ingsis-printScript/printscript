package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult

class ArgumentValidator : TokenValidator {
    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val openingToken = statementBuffer.lookahead(position)
        val closingToken = statementBuffer.lookahead(statementBuffer.size - 2) // agregar find(;)?? -> daría index
        // TODO (fix the fuck out of this. Or get rid of it and fix the fuck out of ExprValidator)
        if (isParenToken(openingToken, "(") && isParenToken(closingToken, ")")) {
            val expression = statementBuffer.subList(position + 1, statementBuffer.size - 2) // diu diu diu
            val validator = ExpressionValidator()
            return when (val result = validator.validate(expression, 0)) {
                is ValidationResult.Success -> ValidationResult.Success(result.consumed + 2)
                is ValidationResult.Error -> ValidationResult.Error(result.message, result.position + 2)
            }
        } else {
            return ValidationResult.Error(
                "Expected enclosing ( ), found '${openingToken?.value}' and '${closingToken?.value}'",
                position
            )
        }
    }

    override fun getExpectedDescription(): String {
        return "( expression )"
    }

    private fun isParenToken(givenToken: Token?, expectedValue: String): Boolean {
        if (givenToken == null) return false
        return isPunctuation(givenToken) && isExpectedValue(givenToken, expectedValue)
    }

    private fun isPunctuation(givenToken: Token) = givenToken.type == TokenType.PUNCTUATION

    private fun isExpectedValue(given: Token, expected: String) = given.value == expected
}
