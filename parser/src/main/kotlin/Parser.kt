package org.example.parser

import com.sun.tools.example.debug.expr.ExpressionParser
import org.example.common.ast.ASTNode
import org.example.common.ast.Program
import org.example.common.ast.statements.Statement
import org.example.common.tokens.Token
import org.example.parser.parsers.AnalyzeStatementService
import org.example.parser.parsers.FunctionCallParser
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationAssignationParser
import org.example.parser.parsers.VariableDeclarationParser

class Parser {

    val validatorList: List<StatementParser> = listOf(FunctionCallParser(), //no se si hacer que el parse la reciba?
        VariableAssignationParser(), VariableDeclarationAssignationParser(), VariableDeclarationParser())

    fun parse(tokenList: List<Token>): Program {
        val segments: List<List<Token>> = separate(tokenList)
        val programList = mutableListOf<Statement>()

        for (statement in segments) {
            val node: ASTNode = parseStatement(statement, validatorList)
            programList.add(node as Statement)
        }
        return Program(programList)
    }


    fun separate(tokens: List<Token>): List<List<Token>> {
        if (tokens.isEmpty()) return emptyList()

        val statements = mutableListOf<List<Token>>()
        val currentStatement = mutableListOf<Token>()

        for (token in tokens) {
            currentStatement.add(token)

            //TODO("hacer rules.rule? -> o sea, inyectar END condition")
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


// TODO("Finish..., para parseStatement manejar errores del analyze")
    val astNodes = org.example.common.ast.statements.map { statement ->
        parseStatement(statement) // ← Acá usa los StatementParsers
    }

    //TODO("Hacer que reciba lista de parsers... ")
    private fun parseStatement(statement: List<Token>, parser: List<StatementParser>): ASTNode {
        val canParse: Boolean = parser.canParse(statement)
        if (canParse) {
            val analysisResult: ValidationResult = AnalyzeStatementService.analyzeStatement(statement, parser.getPattern)
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