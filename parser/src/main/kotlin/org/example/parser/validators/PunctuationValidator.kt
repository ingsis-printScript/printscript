package org.example.parser.validators

import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token
import org.example.token.TokenType

class PunctuationValidator(private val expected: String) : TokenValidator {
    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token
        try {
            token = statementBuffer.lookahead(position)
        } catch (e: NoMoreTokensAvailableException) {
            return ValidationResult.Error("punctuation expected, but reached end of statement", position)
        }
        return if (token.type == TokenType.PUNCTUATION) {
            if (token.value == expected) {
                ValidationResult.Success(listOf(token))
            } else {
                ValidationResult.Error("Expected '$expected', found '${token.value}'", position)
            }
        } else {
            ValidationResult.Error(
                "Expected punctuation '$expected'," +
                    " found ${token.type}",
                position
            )
        }
    }

    override fun getExpectedDescription(): String = "Expected punctuation '$expected'"
}
