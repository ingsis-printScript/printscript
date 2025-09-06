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

    // isEndToken podría ajustarse con args -> recibir expected (TokenType y symbol)

    fun parse(tokenList: List<Token>): Result {
        if (tokenList.isEmpty()) { return Error("Empty token list") }
        try {
            val node: ASTNode = parseStatement(tokenList, parsers)
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

    private fun parseStatement(statement: List<Token>, parsers: List<StatementParser>): ASTNode {
        var bestError: ValidationResult.Error? = null
        var maxTokensConsumed = -1

        for (parser in parsers) {
            val result = AnalyzeStatementService.analyzeStatement(statement, parser.getPattern())

            when (result) {
                is ValidationResult.Success -> return parser.buildAST(statement)
                is ValidationResult.Error -> {
                    bestError = updateBestError(result, bestError, maxTokensConsumed)
                        .also { maxTokensConsumed = maxOf(maxTokensConsumed, result.position) }
                }
            }
        }
        throw createParsingException(bestError, maxTokensConsumed, statement)
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
        maxTokensConsumed: Int,
        statement: List<Token>
    ): SyntaxException {
        return if (bestError == null || maxTokensConsumed == 0) {
            SyntaxException("Invalid structure for statement: $statement")
        } else {
            errorAt(bestError.message, bestError.position)
        }
    }
}
