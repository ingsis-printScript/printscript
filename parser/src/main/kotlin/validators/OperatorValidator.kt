package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.enums.Operator

class OperatorValidator() : TokenValidator {
    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        return when (val token: Token = statement[position]) {
            is OperatorToken -> {
                ValidationResult.Success(1)
            }
            else -> {
                val operations = Operator.entries.map { entry -> entry.symbol }
                ValidationResult.Error("Expected operator '$operations'," +
                        " found ${token::class.simpleName}", position)
            }
        }
    }

    override fun getExpectedDescription(): String = "Expected operator '${expectedSymbol}'"
}