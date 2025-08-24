package org.example.parser.parsers

import org.example.common.tokens.Token
import org.example.parser.ValidationResult

internal class AnalyzeStatementService {
    companion object {
        fun analyzeStatement(statement: List<Token>, pattern: StatementPattern): ValidationResult {
            if (statement.size < pattern.validators.size) {
                return ValidationResult.Error(
                    "Expected ${pattern.validators.size} tokens, found ${statement.size}",
                    0
                )
            }
            var position = 0
            pattern.validators.forEachIndexed { _, validator -> // TODO("iterate pattern, catch missing tokens")
                if (statement.size <= position) {
                    return ValidationResult.Error(
                        "Expected more tokens, found ${statement.size} at position $position",
                        position
                    )
                }
                when (val result = validator.validate(statement, position)) {
                    is ValidationResult.Error -> return result
                    is ValidationResult.Success -> position += result.consumed
                }
            }
            return ValidationResult.Success(position)
        }
    }
}
