package org.example.parser

import org.example.token.Token

sealed class ValidationResult {
    data class Success(
        val consumed: List<Token>
    ) : ValidationResult()

    data class Error(
        val message: String,
        val position: Int
    ) : ValidationResult()
}
