package org.example.parser.validators

import org.example.common.enums.Operator
import org.example.common.enums.Type
import org.example.common.tokens.Token
import org.example.common.enums.TokenType
import org.example.parser.ValidationResult

class TypeValidator : TokenValidator {

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.type == TokenType.SYMBOL) {
            if (Type.fromString(token.value) != null) {
                ValidationResult.Success(1)
            } else {
                ValidationResult.Error(
                    "Expected type (${Type.entries.joinToString { it.name }}), " +
                        "found '${token.value}'",
                    position
                )
            }
        } else {
            ValidationResult.Error("Expected type annotation, found ${token.type}", position)
        }
    }

    override fun getExpectedDescription(): String = "Expected (${Operator.entries.joinToString { it.symbol }})"
}
