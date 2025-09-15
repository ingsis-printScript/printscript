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
        val outcome = analyzeStatement(statementBuffer, parsers)
        commitTokens(statementBuffer, outcome)
        val successfulAnalysisOutcome = successOrThrowBest(outcome)
        return buildAST(successfulAnalysisOutcome)
    }

    fun analyzeStatement(buffer: TokenBuffer, parsers: List<StatementParser>, startingToken: Int = 1): AnalysisOutcome {
        val errors: MutableList<AnalysisOutcome.Error> = mutableListOf()

        for (parser in parsers) {
            when (val result = parser.analyze(buffer, startingToken)) {
                is ValidationResult.Success -> return AnalysisOutcome.Success(result, parser)
                is ValidationResult.Error -> errors.add(AnalysisOutcome.Error(result))
            }
        }

        return bestError(errors)
    }

    private fun buildAST(outcome: AnalysisOutcome.Success): ASTNode {
        val statementTokens = outcome.result.consumed
        return outcome.parser.buildAST(statementTokens)
    }

    // --- Express most accurate error if analysis fails ---

    private fun bestError(errors: List<AnalysisOutcome.Error>): AnalysisOutcome.Error {
        var bestError: AnalysisOutcome.Error = errors.first()
        var maxTokensConsumed = -1
        for (error in errors) {
            bestError = update(error, bestError, maxTokensConsumed)
                .also { maxTokensConsumed = maxOf(maxTokensConsumed, error.result.position) }
        }
        return bestError
    }

    private fun update(
        currentError: AnalysisOutcome.Error,
        bestError: AnalysisOutcome.Error,
        maxTokensConsumed: Int
    ): AnalysisOutcome.Error {
        return if (gotFurther(currentError, maxTokensConsumed)) currentError else bestError
    }

    private fun gotFurther(currentError: AnalysisOutcome.Error, maxTokensConsumed: Int) =
        currentError.result.position > maxTokensConsumed

    private fun commitTokens(buffer: TokenBuffer, outcome: AnalysisOutcome) {
        when (outcome) {
            is AnalysisOutcome.Success -> buffer.commit(outcome.result.consumed.size)
            is AnalysisOutcome.Error -> buffer.commit(outcome.result.position)
        }
    }

    private fun successOrThrowBest(outcome: AnalysisOutcome): AnalysisOutcome.Success {
        return when (outcome) {
            is AnalysisOutcome.Success -> outcome
            is AnalysisOutcome.Error -> throw createParsingException(outcome.result, outcome.result.position)
        }
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

    fun getTokenBuffer(): TokenBuffer = tokenBuffer
    fun getParsers(): List<StatementParser> = parsers
}
