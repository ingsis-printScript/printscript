package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.statements.Statement
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.token.Token
import org.example.parser.exceptions.SyntaxException
import org.example.parser.exceptions.errorAt
import org.example.parser.parsers.AnalyzeStatementService
import org.example.parser.parsers.StatementParser

class Parser(val parsers: List<StatementParser>) {

    fun parse(tokenBuffer: TokenBuffer): Result {
        if (!tokenBuffer.hasNext()) { return Error("No tokens to parse") }
        try {
            val node: ASTNode = parseStatement(tokenBuffer, parsers)
            return Success(node as Statement)
        } catch (e: SyntaxException) {
            return Error(e.message ?: "Unknown error")
        } catch (e: Exception) { // entiendo que los errores de lexer se agarran acá...
            return Error(e.message ?: "Unknown error")
        }
    }

    //TODO: chau canParse, devolver el error del que falle last...
    // el que mas lejos llega (pero sin success, no llegó al final) me indica el error que obtuve.
    // si sale bien, tiro success
    // no can parse

    private fun parseStatement(statementBuffer: TokenBuffer, parsers: List<StatementParser>): ASTNode {
        var bestError: ValidationResult.Error? = null
        var maxTokensConsumed = -1

        for (parser in parsers) {
            val result = AnalyzeStatementService.analyzeStatement(statementBuffer, parser.getPattern())

            when (result) {
                is ValidationResult.Success -> return parser.buildAST(statementBuffer)
                is ValidationResult.Error -> {
                    bestError = updateBestError(result, bestError, maxTokensConsumed)
                        .also { maxTokensConsumed = maxOf(maxTokensConsumed, result.position) }
                }
            }
        }
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
            errorAt(bestError.message, bestError.position)
        }
    }
}
