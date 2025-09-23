package org.example.parser.validators

import org.example.common.enums.Operator
import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token
import org.example.token.TokenType

class OperatorValidator : TokenValidator {
    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token
        try {
            token = statementBuffer.lookahead(position)
        } catch (e: NoMoreTokensAvailableException) {
            return ValidationResult.Error("operator expected, but reached end of statement", position)
        }
        return if (token.type == TokenType.OPERATOR) {
            if (Operator.fromString(token.value) != null) {
                ValidationResult.Success(listOf(token))
            } else {
                ValidationResult.Error(
                    "Expected operator (${Operator.entries.joinToString { it.symbol }}), " +
                        "found '${token.value}'",
                    position
                )
            }
        } else {
            ValidationResult.Error(
                "Expected operator, found '${token.type}'",
                position
            )
        }
    }

    override fun getExpectedDescription(): String = "Expected operator"
}
