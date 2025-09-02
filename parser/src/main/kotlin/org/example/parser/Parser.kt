package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.statements.Statement
import org.example.token.Token
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.AnalyzeStatementService
import org.example.parser.parsers.StatementParser

class Parser(val parsers: List<StatementParser>) {

    // isEndToken podría ajustarse con args -> recibir expected (TokenType y symbol)

    fun parse(tokenList: List<Token>): Statement {
        if (tokenList.isEmpty()) { throw SyntaxException("Empty token list") }
        val node: ASTNode = parseStatement(tokenList, parsers)
        return node as Statement
    }

    //TODO: chau canParse, devolver el error del que falle last...
    // el que mas lejos llega (pero sin success, no llegó al final) me indica el error que obtuve.
    // si sale bien, tiro success
    // no can parse

    // dejé canParse porque así los errores propios de un tipo de statement se pueden comunicar
    private fun parseStatement(statement: List<Token>, parsers: List<StatementParser>): ASTNode {
        for (parser in parsers) {
            if (parser.canParse(statement)) {
                val analysisResult: ValidationResult = AnalyzeStatementService
                    .analyzeStatement(statement, parser.getPattern())
                if (analysisResult is ValidationResult.Error) {
                    throw SyntaxException(
                        "Error in statement: " +
                            "${analysisResult.message} at index ${analysisResult.position}"
                    )
                }
                return parser.buildAST(statement)
            }
        }
        throw SyntaxException("Invalid structure for statement: $statement")
    }
}
