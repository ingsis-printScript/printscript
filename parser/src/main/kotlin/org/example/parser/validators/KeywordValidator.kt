package org.example.parser.validators

import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token
import org.example.token.TokenType

class KeywordValidator(private val expectedKeywords: Set<String>) : TokenValidator {

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token
        try {
            token = statementBuffer.lookahead(position)
        } catch (e: NoMoreTokensAvailableException) {
            return ValidationResult.Error("keyword expected, but reached end of statement", position)
        }

        return if (token.type == TokenType.KEYWORD) {
            if (token.value in expectedKeywords) {
                ValidationResult.Success(listOf(token))
            } else {
                ValidationResult.Error("Expected one of $expectedKeywords, found '${token.value}'", position)
            }
        } else {
            ValidationResult.Error("Expected keyword, found ${token.type}", position)
        }
    }

    override fun getExpectedDescription(): String {
        return "Expected one of $expectedKeywords"
    }
}
