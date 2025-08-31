package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.ValidationResult
import org.example.token.Token

class SymbolValidator: TokenValidator {

    private val symbolPattern = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return when {
            token.type == TokenType.SYMBOL && isValidSymbolFormat(token.value) -> {
                ValidationResult.Success(1)
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