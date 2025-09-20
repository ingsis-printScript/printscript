package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class StringValidator : TokenValidator {

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token = try {
            statementBuffer.lookahead(position)
        } catch (_: NoMoreTokensAvailableException) {
            return ValidationResult.Error("string expected, but reached end of statement", position)
        }

        return if (token.type == TokenType.STRING) {
            ValidationResult.Success(listOf(token))
        } else {
            ValidationResult.Error("Invalid string: '${token.value}'", position)
        }
    }

    override fun getExpectedDescription(): String =
        "Expected string literal"
}
