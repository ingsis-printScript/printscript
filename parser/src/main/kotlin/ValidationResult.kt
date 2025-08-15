package org.example.parser

sealed class ValidationResult{
    data class Success(
        val consumed: Int
    ) : ValidationResult()

    data class Error(
        val message: String,
        val position: Int
    ) : ValidationResult()
}