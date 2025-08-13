package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.enums.Punctuation

class PunctuationValidator(private val expected: Punctuation) : TokenValidator {
    private val expectedSymbol = expected.symbol
    override fun validate(token: Token, position: Int): ValidationResult {
        return when (token) {
            is PunctuationToken -> {
                if (token.kind.symbol == expectedSymbol) {
                    ValidationResult.Success
                } else {
                    ValidationResult.Error("Expected '${expectedSymbol}', found '${token.kind.symbol}'", position)
                }
            }
            else -> ValidationResult.Error("Expected punctuation '${expectedSymbol}'," +
                    " found ${token::class.simpleName}", position)
        }
    }

    override fun getExpectedDescription(): String = "Expected punctuation '${expectedSymbol}'"
}