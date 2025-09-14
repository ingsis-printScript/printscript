package org.example.parser.validators

import org.example.common.enums.TokenType
import org.example.parser.AnalysisOutcome
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.ValidationResult
import org.example.parser.parsers.StatementParser
import org.example.token.Token

class BlockValidator(
    private val statementParsers: List<StatementParser>,
    private val isBlockEnd: (Token) -> Boolean = { t -> t.type == TokenType.PUNCTUATION && t.value == "}" }
    // TODO: alternative: detect tie in parser, detect "no valid statements" here, break
) : TokenValidator {
    override fun validate(statementBuffer: TokenBuffer, position: Int): ValidationResult {
        val parser = Parser(statementParsers, statementBuffer)
        var index = position
        var consumed = mutableListOf<Token>()
        while (!statementBuffer.isAtEnd(index)) {
            if (isBlockEnd(statementBuffer.lookahead(index))) { break }
            val outcome = parser.analyzeStatement(statementBuffer, statementParsers, index)
            when (outcome) {
                is AnalysisOutcome.Error -> {
                    val result = outcome.result
                    index += result.position
                    return ValidationResult.Error(result.message, index)
                }
                is AnalysisOutcome.Success -> {
                    consumed.addAll(outcome.result.consumed)
                    index += consumed(outcome.result)
                }
            }
        }
        return ValidationResult.Success(consumed)
    }

    private fun consumed(validation: ValidationResult.Success) = validation.consumed.size

    override fun getExpectedDescription(): String {
        return "Valid block of statements"
    }
}
