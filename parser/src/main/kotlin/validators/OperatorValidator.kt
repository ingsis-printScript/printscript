package org.example.parser.validators

import enums.Operator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType

class OperatorValidator() : TokenValidator {
    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.kind == TokenType.OPERATOR) {
            if (Operator.fromString(token.kind.name) != null) {
                ValidationResult.Success(1)
            } else {
                ValidationResult.Error(
                    "Expected operator (${Operator.entries.joinToString { it.symbol }}), found '${token.name}'", position)
            }
        }
        else {
            ValidationResult.Error(
                "Expected operator, found '${token.kind}'", position)
        }
    }


    override fun getExpectedDescription(): String = "Expected operator"
}