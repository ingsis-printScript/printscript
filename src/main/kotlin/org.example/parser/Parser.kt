package org.example.parser

import org.example.common.ast.ASTnode
import org.example.common.ast.Program
import org.example.common.tokens.PunctuationToken
import org.example.common.tokens.Token
import org.example.common.tokens.enums.Punctuation

class Parser {


    //recibimos una lista de tokens
    //la dividimos en segmentos si termina en punto y coma
    //cada segmento es procesado -> AST

    fun parse(tokenList: List<Token>): ASTnode {
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

            //TODO("hacer rules.rule?")
            if (token is PunctuationToken && token.type == Punctuation.SEMICOLON) {
                statements.add(currentStatement.toList())
                currentStatement.clear()
            }
        }

        if (currentStatement.isNotEmpty()) {
            statements.add(currentStatement.toList())
        }

        return statements
    }



    val astNodes = org.example.common.ast.statements.map { statement ->
        parseStatement(statement) // ← Acá usa los StatementParsers
    }

    private fun parseStatement(statement: List<Token>): ASTNode {
        if (parser.canParse(statement) && parser.analyzeStatement(statement)) { // separar(?
            return parser.parse(statement) // ← Usa el StatementParser específico
        }
        throw SyntaxError("No parser found for statement: ${statement}")
    }

}


//} let a: Int = 2
//} a = 3
//} print(a)
//} let a: Int