package org.example.parser.validators

import org.example.common.tokens.OperatorToken
import org.example.common.tokens.Token
import org.example.common.tokens.enums.Operator

class OperatorValidator(private val expected: Operator) : TokenValidator {
    private val expectedSymbol = expected.symbol

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        return when (val token: Token = statement[position]) {
            is OperatorToken -> {
                if (token.kind.symbol == expectedSymbol) {
                    ValidationResult.Success(1)
                } else {
                    ValidationResult.Error("Expected '${expectedSymbol}', found '${token.kind.symbol}'", position)
                }
            }
            else -> ValidationResult.Error("Expected operator '${expectedSymbol}'," +
                    " found ${token::class.simpleName}", position)
        }
    }

    override fun getExpectedDescription(): String = "Expected operator '${expectedSymbol}'"
}