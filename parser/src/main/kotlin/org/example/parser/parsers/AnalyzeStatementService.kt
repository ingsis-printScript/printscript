package org.example.parser.parsers

import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult

internal class AnalyzeStatementService {
    companion object {
        fun analyzeStatement(statementBuffer: TokenBuffer, pattern: StatementPattern): ValidationResult {
            var position = 1
            pattern.validators.forEachIndexed { _, validator ->
                when (val result = validator.validate(statementBuffer, position)) {
                    is ValidationResult.Error -> return result
                    is ValidationResult.Success -> position += result.consumed
                }
            }
            return ValidationResult.Success(position)
        }
    }
}
