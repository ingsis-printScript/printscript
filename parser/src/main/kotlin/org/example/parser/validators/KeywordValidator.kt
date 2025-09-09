package org.example.parser.validators

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.enums.keywords.DeclaratorKeyword
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult

class KeywordValidator() : TokenValidator {

    private val expectedKeyword = DeclaratorKeyword

    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val token: Token = statementBuffer.lookahead(position)
        return if (token.type == TokenType.KEYWORD) {
            if (expectedKeyword.isDeclaratorKeyword(token.value)) {
                ValidationResult.Success(listOf(token))
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
