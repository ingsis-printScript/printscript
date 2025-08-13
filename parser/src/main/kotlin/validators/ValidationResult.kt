package org.example.parser.validators

sealed class ValidationResult{
    data class Success(
        val consumed: Int
    ) : ValidationResult()

    data class Error(
        val message: String,
        val position: Int
    ) : ValidationResult()
}