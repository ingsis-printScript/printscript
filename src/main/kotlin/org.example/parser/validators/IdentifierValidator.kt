package org.example.parser.validators

import org.example.common.tokens.Token

class IdentifierValidator() : TokenValidator {
    // por ahora lo dejo medio hardcodeado, pero dsp se podría pasar como parámetro
    private val identifierPattern = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")

    override fun validate(token: Token, position: Int): ValidationResult {
        return when (token) {
            is IdentifierToken -> {
                if (isValidIdentifierFormat(token.name)) {
                    ValidationResult.Success
                } else {
                    ValidationResult.Error("Invalid identifier format: '${token.name}'", position)
                }
            }
            else -> ValidationResult.Error("Expected identifier, found ${token::class.simpleName}", position)
        }
    }

    private fun isValidIdentifierFormat(name: String): Boolean {
        return name.matches(identifierPattern)
    }

    override fun getExpectedDescription(): String {
        return "Expected valid identifier format: ${identifierPattern.pattern}"
    }
}