package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class BooleanValidator : TokenValidator {

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token = statementBuffer.lookahead(position)
        return if (token.type == TokenType.BOOLEAN && (token.value == "true" || token.value == "false")) {
            ValidationResult.Success(listOf(token))
        } else {
            ValidationResult.Error("boolean expected, but found ${token.type}", position)
        }
    }

    override fun getExpectedDescription(): String {
        return "Expecting boolean: true or false"
    }
}
