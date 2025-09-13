package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class TypeValidator(private val expectedTypes: Set<String>) : TokenValidator {

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token
        try {
            token = statementBuffer.lookahead(position)
        } catch(e: NoMoreTokensAvailableException) {
            return ValidationResult.Error("type expected, but reached end of statement", position)
        }
        return if (token.type == TokenType.SYMBOL) {
            if (token.value in expectedTypes) {
                ValidationResult.Success(listOf(token))
            } else {
                ValidationResult.Error(
                    "Expected types $expectedTypes but found ${token.value}",
                    position
                )
            }
        } else {
            ValidationResult.Error("Expected type annotation, found ${token.type}", position)
        }
    }

    override fun getExpectedDescription(): String = "Expected $expectedTypes"
}
