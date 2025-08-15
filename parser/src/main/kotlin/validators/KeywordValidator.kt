package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.ValidationResult

// TODO(odio sumar una dep. pero ig que esta ok? o sea, es el validator de ESE token)
class KeywordValidator(private val expectedKeyword: String) : TokenValidator {

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.kind == TokenType.KEYWORD) {
            if (token.kind.name.equals(expectedKeyword, ignoreCase = true)) {
                ValidationResult.Success(1)
            } else {
                ValidationResult.Error("Expected '$expectedKeyword', found '${token.kind}'", position)
            }
        } else ValidationResult.Error("Expected keyword, found ${token.kind}", position)
    }

    override fun getExpectedDescription(): String {
        return "Expected keyword '$expectedKeyword'"
    }
}