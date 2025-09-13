package org.example.parser

import org.example.parser.parsers.StatementParser

sealed class AnalysisOutcome {
    data class Success(
        val result: ValidationResult.Success,
        val parser: StatementParser
    ) : AnalysisOutcome()

    data class Error(
        val result: ValidationResult.Error
    ) : AnalysisOutcome()
}
