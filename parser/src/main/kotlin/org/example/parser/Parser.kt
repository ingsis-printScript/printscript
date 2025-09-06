package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.statements.Statement
import org.example.common.results.Error
import org.example.common.results.Result
import org.example.common.results.Success
import org.example.parser.exceptions.SyntaxException
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


    private fun parseStatement(statementBuffer: TokenBuffer, parsers: List<StatementParser>): ASTNode {
        var bestError: ValidationResult.Error? = null //maneja un nullllll
        var maxTokensConsumed = -1

        for (parser in parsers) {
            val result = AnalyzeStatementService.analyzeStatement(statementBuffer, parser.getPattern())

            when (result) {
                //TODO(Crear una lista que tenga todos los tokens que se consumieron y ahi buildear el ast)
                is ValidationResult.Success -> return parser.buildAST(statementBuffer)
                is ValidationResult.Error -> {
                    bestError = updateBestError(result, bestError, maxTokensConsumed)
                        .also { maxTokensConsumed = maxOf(maxTokensConsumed, result.position) } //TODO(Chequear tests)
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
            SyntaxException.errorAt(bestError.message, bestError.position)
        }
    }
}
