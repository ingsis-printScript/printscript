package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.ValidationResult
import org.example.token.Token

class NumberValidator: TokenValidator {

    private val numberPattern = Regex("^[0-9]+(\\.[0-9]+)?$")

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return when {
            token.type == TokenType.NUMBER && isValNumberFormat(token.value) -> {
                ValidationResult.Success(1)
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