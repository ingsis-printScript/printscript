package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

class StringValidator : TokenValidator {

       private val stringPattern = Regex("^\\(.*\\)$")

       override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
           val token: Token = statementBuffer.lookahead(position)
           return when {
               token.type == TokenType.STRING && isValidStringFormat(token.value) -> {
                   ValidationResult.Success(1)
               }
               else -> {
                   ValidationResult.Error("Invalid string format: '${token.value}'", position)
               }
           }
       }

       private fun isValidStringFormat(name: String): Boolean {
           return name.matches(stringPattern)
       }

       override fun getExpectedDescription(): String {
           return "Expected valid string format: ${stringPattern.pattern}"
       }
   }