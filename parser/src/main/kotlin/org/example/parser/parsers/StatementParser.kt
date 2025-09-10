package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.token.Token

interface StatementParser {
    fun buildAST(statements: List<Token>): ASTNode
    fun getPatterns(): List<StatementPattern>

    fun analyze(buffer: TokenBuffer): ValidationResult {
        val errors = mutableListOf<ValidationResult.Error>()

        for (pattern in this.getPatterns()) {
            when (val result = pattern.analyzeStatement(buffer)) {
                is ValidationResult.Success -> return result
                is ValidationResult.Error -> errors.add(result)
            }
        }

        val bestError = errors.maxByOrNull { it.message.length }
        return ValidationResult.Error(
            bestError?.message ?: "Invalid structure for statement",
            bestError?.position ?: 0
        )
    }
}