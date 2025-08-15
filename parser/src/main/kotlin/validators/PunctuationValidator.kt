package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.TokenType

class PunctuationValidator(private val expected: String) : TokenValidator {
    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.kind == TokenType.PUNCTUATION) {
                if (token.name.equals(expected)) {
                    ValidationResult.Success(1)
                } else {
                    ValidationResult.Error("Expected '${expected}', found '${token.name}'", position)
                }
            }
            else ValidationResult.Error("Expected punctuation '${expected}'," +
                    " found ${token.kind}", position)
        }

    override fun getExpectedDescription(): String = "Expected punctuation '${expected}'"
}