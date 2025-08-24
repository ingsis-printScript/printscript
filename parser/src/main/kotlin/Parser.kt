package org.example.parser

import org.example.common.ast.ASTNode
import org.example.common.ast.Program
import org.example.common.ast.statements.Statement
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.AnalyzeStatementService
import org.example.parser.parsers.StatementParser

class Parser(val parsers: List<StatementParser>) {

    // recibimos una lista de tokens
    // la dividimos en segmentos si termina en punto y coma
    //      (así acepta varios statements, aunque solo le mandemos 1)
    // cada segmento es procesado -> AST -> Program

    // isEndToken podría ajustarse con args -> recibir expected (TokenType y symbol)

    fun parse(tokenList: List<Token>): Program {
        val segments: List<List<Token>> = separate(tokenList)
        val programList = mutableListOf<Statement>()

        for (statement in segments) {
            val node: ASTNode = parseStatement(statement, parsers)
            programList.add(node as Statement)
        }
        return Program(programList)
    }


    private fun separate(tokens: List<Token>): List<List<Token>> {
        if (tokens.isEmpty()) return emptyList()

        val statements = mutableListOf<List<Token>>()
        val currentStatement = mutableListOf<Token>()

        for (token in tokens) {
            currentStatement.add(token)

            //TODO("hacer rules.rule? -> o sea, inyectar END condition")
            if (isEndToken(token)) {
                statements.add(currentStatement.toList())
                currentStatement.clear()
            }
        }

        if (currentStatement.isNotEmpty()) {
            statements.add(currentStatement.toList())
        }

        return statements
    }

    private fun isEndToken(token: Token) = token.type == TokenType.PUNCTUATION && token.value == ";"


    //List<StatementParser>
    //canParse
    //analyzeStatement
    //buildAST

    // dejé canParse porque así los errores propios de un tipo de statement se pueden comunicar
    private fun parseStatement(statement: List<Token>, parsers: List<StatementParser>): ASTNode {
        for (parser in parsers){
            if (parser.canParse(statement)) {
                val analysisResult: ValidationResult = AnalyzeStatementService.analyzeStatement(statement, parser.getPattern())
                if (analysisResult is ValidationResult.Error) {
                    throw SyntaxException("Error in statement: ${analysisResult.message} at index ${analysisResult.position}")
                }
                return parser.buildAST(statement)
            }
        }
        throw SyntaxException("Invalid structure for statement: $statement")
    }
}


//} let a: Int = 2
//} a = 3
//} print(a)
//} let a: Int