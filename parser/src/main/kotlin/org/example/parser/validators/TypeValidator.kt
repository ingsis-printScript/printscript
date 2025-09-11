package org.example.parser.validators

import org.example.common.enums.Operator
import org.example.common.enums.TokenType
import org.example.common.enums.Type
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class TypeValidator : TokenValidator {

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token = statementBuffer.lookahead(position)
        return if (token.type == TokenType.SYMBOL) {
            if (Type.fromString(token.value) != null) {
                ValidationResult.Success(listOf(token))
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
