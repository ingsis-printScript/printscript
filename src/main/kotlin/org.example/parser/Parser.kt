package org.example.parser

import org.example.common.ast.ASTNode
import org.example.common.ast.Program
import org.example.common.tokens.PunctuationToken
import org.example.common.tokens.Token
import org.example.common.tokens.enums.Punctuation
import org.example.parser.parsers.StatementParser
import org.example.parser.validators.ValidationResult

class Parser {


    //recibimos una lista de tokens
    //la dividimos en segmentos si termina en punto y coma
    //cada segmento es procesado -> AST

    fun parse(tokenList: List<Token>): ASTNode {
        val segments: List<List<Token>> = separate(tokenList)

        for (statement in segments) {parseStatement(statement) }
        return Program()
    }


    fun separate(tokens: List<Token>): List<List<Token>> {
        if (tokens.isEmpty()) return emptyList()

        val statements = mutableListOf<List<Token>>()
        val currentStatement = mutableListOf<Token>()

        for (token in tokens) {
            currentStatement.add(token)

            //TODO("hacer rules.rule? -> o sea, inyectar END condition")
            if (token is PunctuationToken && token.kind == Punctuation.SEMICOLON) {
                statements.add(currentStatement.toList())
                currentStatement.clear()
            }
        }

        if (currentStatement.isNotEmpty()) {
            statements.add(currentStatement.toList())
        }

        return statements
    }


// TODO("Finish..., para parseStatement manejar errores del analyze")
    val astNodes = org.example.common.ast.statements.map { statement ->
        parseStatement(statement) // ← Acá usa los StatementParsers
    }

    private fun parseStatement(statement: List<Token>, parser: StatementParser): ASTNode {
        val canParse: Boolean = parser.canParse(statement)
        if (canParse) {
            val analysisResult:ValidationResult = parser.analyzeStatement(statement)
            if (analysisResult is ValidationResult.Error) {
                // TODO(Check el tema de position... y el tipo de error que devuelvo)
                throw SyntaxError("Error in statement: ${analysisResult.message} at index ${analysisResult.position}")
            }
            return parser.buildAST(statement)
        }
        throw SyntaxError("No parser found for statement: $statement")
    }

}


//} let a: Int = 2
//} a = 3
//} print(a)
//} let a: Int