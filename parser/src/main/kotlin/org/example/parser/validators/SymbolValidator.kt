package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class SymbolValidator : TokenValidator {

    private val symbolPattern = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token
        try {
            token = statementBuffer.lookahead(position)
        } catch(e: NoMoreTokensAvailableException) {
            return ValidationResult.Error("symbol expected, but reached end of statement", position)
        }
        return when {
            token.type == TokenType.SYMBOL && isValidSymbolFormat(token.value) -> {
                ValidationResult.Success(listOf(token))
            }
            else -> {
                ValidationResult.Error("Invalid symbol format: '${token.value}'", position)
            }
        }
    }

    private fun isValidSymbolFormat(name: String): Boolean {
        return name.matches(symbolPattern)
    }

    override fun getExpectedDescription(): String {
        return "Expected valid symbol format: ${symbolPattern.pattern}"
    }
}
