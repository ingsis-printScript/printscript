package org.example.parser.validators

import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult

interface TokenValidator {
    fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult
    fun getExpectedDescription(): String
}
