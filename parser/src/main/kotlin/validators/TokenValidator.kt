package org.example.parser.validators

import org.example.common.tokens.Token
import org.example.parser.ValidationResult

interface TokenValidator {
    fun validate(statement: List<Token>, position: Int): ValidationResult
    fun getExpectedDescription(): String
}

