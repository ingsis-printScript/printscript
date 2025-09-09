package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult

class PunctuationValidator(private val expected: String) : TokenValidator {
    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token = statementBuffer.lookahead(position)
        return if (token.type == TokenType.PUNCTUATION) {
            if (token.value.equals(expected)) {
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
