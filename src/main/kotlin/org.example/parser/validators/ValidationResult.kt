package org.example.parser.validators

sealed class ValidationResult{
    data object Success : ValidationResult()

    data class Error(
        val message: String,
        val position: Int
    ) : ValidationResult()
}