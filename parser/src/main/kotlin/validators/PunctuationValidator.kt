package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.ValidationResult

class PunctuationValidator(private val expected: String) : TokenValidator {
    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.type == TokenType.PUNCTUATION) {
            if (token.value.equals(expected)) {
                ValidationResult.Success(1)
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
