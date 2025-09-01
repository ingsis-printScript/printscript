package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.ValidationResult
import org.example.token.Token

class StringValidator: TokenValidator {

       private val stringPattern = Regex(".*")

       override fun validate(statement: List<Token>, position: Int): ValidationResult {
           val token: Token = statement[position]
           return when {
               token.type == TokenType.STRING && token.value.matches(stringPattern) -> {
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