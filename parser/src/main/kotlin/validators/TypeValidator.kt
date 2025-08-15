package org.example.parser.validators

import org.example.common.enums.Operator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.enums.Type

class TypeValidator() : TokenValidator {

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.kind == TokenType.SYMBOL) {
                if (Type.fromString(token.kind.name) != null) {
                    ValidationResult.Success(1)
                } else {
                    ValidationResult.Error("Expected type (${Type.entries.joinToString { it.name }}), found '${token.kind.name}'", position)
                }
            }
            else ValidationResult.Error("Expected type annotation, found ${token.kind}", position)
        }

    override fun getExpectedDescription(): String = "Expected type annotation (${Operator.entries.joinToString { it.symbol }})"
}