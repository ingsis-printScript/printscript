package org.example.parser.validators

import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token
import org.example.token.TokenType

class NumberValidator : TokenValidator {

    private val numberPattern = Regex("^[0-9]+(\\.[0-9]+)?$")

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token
        try {
            token = statementBuffer.lookahead(position)
        } catch (e: NoMoreTokensAvailableException) {
            return ValidationResult.Error("number expected, but reached end of statement", position)
        }
        return when {
            token.type == TokenType.NUMBER && isValNumberFormat(token.value) -> {
                ValidationResult.Success(listOf(token))
            }
            else -> {
                ValidationResult.Error("Invalid number format: '${token.value}'", position)
            }
        }
    }

    private fun isValNumberFormat(name: String): Boolean {
        return name.matches(numberPattern)
    }

    override fun getExpectedDescription(): String {
        return "Expected valid number format: ${numberPattern.pattern}"
    }
}
