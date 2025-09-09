package org.example.parser

import org.example.parser.parsers.StatementPattern
import org.example.token.Token

internal class AnalyzeStatementService {
    companion object {
        fun analyzeStatement(statementBuffer: TokenBuffer, patterns: List<StatementPattern>): ValidationResult {
            val errors = mutableListOf<ValidationResult.Error>()

            for (pattern in patterns) {
                var position = 1
                val consumed = mutableListOf<Token>()
                var failed = false

                for (validator in pattern.validators) {
                    when (val result = validator.validate(statementBuffer, position)) {
                        is ValidationResult.Error -> {
                            errors.add(result)
                            failed = true
                            break // este patrón no sirve, probamos con el siguiente
                        }
                        is ValidationResult.Success -> {
                            position += result.consumed.size
                            consumed.addAll(result.consumed)
                        }
                    }
                }

                if (!failed) {
                    // Este patrón funcionó
                    return ValidationResult.Success(consumed)
                }
            }

            // Ningún patrón funcionó → devolvemos el error más informativo
            val errorMessage = errors.maxByOrNull { it.message.length }
            return ValidationResult.Error(
                errorMessage?.message ?: "Invalid structure for statement",
                errorMessage?.position ?: 0
            )
        }
    }
}
