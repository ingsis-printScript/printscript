package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.statements.Statement
import org.example.common.PrintScriptIterator
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.StatementParser

class Parser(
    private val parsers: List<StatementParser>,
    private val tokenBuffer: TokenBuffer
) : PrintScriptIterator<ASTNode> {

    override fun hasNext(): Boolean {
        // duda: miro si buffer tiene next o si recibo un ASTNode?
        // si lo cambio a que haya un statement completo valido, update condition parse()
        return tokenBuffer.hasNext()
    }

    override fun getNext(): ASTNode {
        // dudas:
        // return ASTNode o Result? (keep 'parse' intact, or use it?)
        // if ASTNode, upon error, throw exception?
        return parseStatement(tokenBuffer, parsers)
    }

    // Safe and type-oriented/expressive version of getNext()
    // Still used in testing (for individual statements), and possibly in validating without interpreting
    // Useful for error detection, nicer to interact with return type that exception
    fun parse(): Result {
        if (!hasNext()) { return Error("No tokens to parse") }
        try {
            val node: ASTNode = parseStatement(tokenBuffer, parsers)
            return Success(node as Statement)
        } catch (e: SyntaxException) {
            return Error(e.message ?: "Unknown error")
        } catch (e: Exception) {
            return Error(e.message ?: "Unknown error")
        }
    }

    private fun parseStatement(statementBuffer: TokenBuffer, parsers: List<StatementParser>): ASTNode {
        val successfulAnalysisOutcome = analyzeStatement(statementBuffer, parsers)
        return buildAST(successfulAnalysisOutcome)
    }

    private fun analyzeStatement(buffer: TokenBuffer, parsers: List<StatementParser>): AnalysisOutcome {
        val outcomes: List<AnalysisOutcome> =
            parsers.map { parser ->
                val result = parser.analyze(buffer)
                AnalysisOutcome(parser, result)
            }

        return successOrThrowBestError(outcomes, buffer)
    }

    private fun buildAST(outcome: AnalysisOutcome): ASTNode {
        val analysisResult = outcome.validation as ValidationResult.Success
        val statementTokens = analysisResult.consumed
        return outcome.parser.buildAST(statementTokens)
    }

    // --- Express most accurate error if analysis fails ---

    private fun successOrThrowBestError(outcomes: List<AnalysisOutcome>, buffer: TokenBuffer): AnalysisOutcome {
        var bestError: ValidationResult.Error? = null
        var maxTokensConsumed = -1
        for (outcome in outcomes) {
            when (val result = outcome.validation) {
                is ValidationResult.Success -> {
                    buffer.commit(result.consumed.size)
                    return outcome
                }
                is ValidationResult.Error -> {
                    bestError = updateBestError(result, bestError, maxTokensConsumed)
                        .also { maxTokensConsumed = maxOf(maxTokensConsumed, result.position) }
                }
            }
        }
        buffer.commit(maxTokensConsumed)
        throw createParsingException(bestError, maxTokensConsumed)
    }

    private fun updateBestError(
        currentError: ValidationResult.Error,
        bestError: ValidationResult.Error?,
        maxTokensConsumed: Int
    ): ValidationResult.Error? {
        return if (currentError.position > maxTokensConsumed) currentError else bestError
    }

    private fun createParsingException(
        bestError: ValidationResult.Error?,
        maxTokensConsumed: Int
    ): SyntaxException {
        return if (bestError == null || maxTokensConsumed == 0) {
            SyntaxException("Invalid structure for statement")
        } else {
            SyntaxException.errorAt(bestError.message, bestError.position)
        }
    }
}
