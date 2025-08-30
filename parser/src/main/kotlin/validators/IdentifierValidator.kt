package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.ValidationResult

class IdentifierValidator : TokenValidator {

    // por ahora lo dejo medio hardcodeado, pero dsp se podría pasar como parámetro

    private val identifierPattern = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.type == TokenType.SYMBOL) {
            if (isValidIdentifierFormat(token.value)) {
                ValidationResult.Success(1)
            } else {
                ValidationResult.Error("Invalid identifier format: '${token.value}'", position)
            }
        } else {
            ValidationResult.Error("Expected identifier, found ${token::class.simpleName}", position)
        }
    }

    private fun isValidIdentifierFormat(name: String): Boolean {
        return name.matches(identifierPattern)
    }

    override fun getExpectedDescription(): String {
        return "Expected valid identifier format: ${identifierPattern.pattern}"
    }
}
