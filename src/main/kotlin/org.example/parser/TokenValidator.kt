package org.example.parser

import org.example.common.tokens.Token

interface TokenValidator {
    fun validate(token: Token, position: Int): ValidationResult
    fun getExpectedDescription(): String
}

