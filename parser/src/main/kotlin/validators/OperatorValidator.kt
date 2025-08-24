package org.example.parser.validators

import org.example.common.enums.Operator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.ValidationResult

class OperatorValidator : TokenValidator {
    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.type == TokenType.OPERATOR) {
            if (Operator.fromString(token.type.name) != null) {
                ValidationResult.Success(1)
            } else {
                ValidationResult.Error(
                    "Expected operator (${Operator.entries.joinToString { it.symbol }}), " +
                            "found '${token.value}'", position)
            }
        }
        else {
            ValidationResult.Error(
                "Expected operator, found '${token.type}'", position)
        }
    }


    override fun getExpectedDescription(): String = "Expected operator"
}
