package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

interface StatementParser {
    fun buildAST(statements: List<Token>): ASTNode
    fun getPatterns(): List<StatementPattern>

    fun analyze(buffer: TokenBuffer, start: Int): ValidationResult {
        val errors = mutableListOf<ValidationResult.Error>()
        val successes = mutableListOf<ValidationResult.Success>()

        for (pattern in this.getPatterns()) {
            when (val result = pattern.analyzeStatement(buffer, start)) {
                is ValidationResult.Success -> successes.add(result)
                is ValidationResult.Error -> errors.add(result)
            }
        }

        if (successes.isNotEmpty()) { return successes.maxBy { it.consumed.size } }

        val bestError = errors.maxByOrNull { it.message.length }
        return ValidationResult.Error(
            bestError?.message ?: "Invalid structure for statement",
            bestError?.position ?: 0
        )
    }
}
