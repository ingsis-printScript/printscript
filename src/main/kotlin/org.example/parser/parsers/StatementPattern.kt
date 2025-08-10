package org.example.parser.parsers

import org.example.parser.validators.TokenValidator

data class StatementPattern(val validators: List<TokenValidator>) {
}