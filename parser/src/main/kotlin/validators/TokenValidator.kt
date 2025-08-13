package org.example.parser.validators

import org.example.common.tokens.Token

interface TokenValidator {
    fun validate(statement: List<Token>, position: Int): ValidationResult
    fun getExpectedDescription(): String
}

