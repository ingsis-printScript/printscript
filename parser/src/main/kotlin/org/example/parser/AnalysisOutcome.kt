package org.example.parser

import org.example.parser.parsers.StatementParser

data class AnalysisOutcome(
    val parser: StatementParser,
    val validation: ValidationResult
)
