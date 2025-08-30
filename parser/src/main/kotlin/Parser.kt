package org.example.parser

import org.example.ast.ASTNode
import org.example.ast.statements.Statement
import org.example.token.Token
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.AnalyzeStatementService
import org.example.parser.parsers.StatementParser

class Parser(val parsers: List<StatementParser>) {

    // recibimos una lista de tokens
    // la dividimos en segmentos si termina en punto y coma
    //      (así acepta varios statements, aunque solo le mandemos 1)
    // cada segmento es procesado -> AST -> Program

    // isEndToken podría ajustarse con args -> recibir expected (TokenType y symbol)

    fun parse(tokenList: List<Token>): Statement {
        val node: ASTNode = parseStatement(tokenList, parsers)
        return node as Statement
    }

    // List<StatementParser>
    // canParse
    // analyzeStatement
    // buildAST

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

// } let a: Int = 2
// } a = 3
// } print(a)
// } let a: Int
