package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.enums.keywords.DeclaratorKeyword
import org.example.parser.ValidationResult

// TODO(odio sumar una dep. pero ig que esta ok? o sea, es el validator de ESE token)
class KeywordValidator() : TokenValidator {

    private val expectedKeyword = DeclaratorKeyword

    override fun validate(statement: List<Token>, position: Int): ValidationResult {
        val token: Token = statement[position]
        return if (token.type == TokenType.KEYWORD) {
            if (expectedKeyword.isDeclaratorKeyword(token.value)) {
                ValidationResult.Success(1)
            } else {
                ValidationResult.Error("Expected '$expectedKeyword', found '${token.value}'", position)
            }
        } else {
            ValidationResult.Error("Expected keyword, found ${token.type}", position)
        }
    }

    override fun getExpectedDescription(): String {
        return "Expected keyword '$expectedKeyword'"
    }

}
