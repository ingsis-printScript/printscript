package org.example.parser.parsers

import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.enums.Type
import org.example.parser.validators.*

class VariableDeclarationParser : StatementParser {

    private val pattern = StatementPattern(listOf(
        KeywordValidator("let"), //mmmm hardcodeado
        IdentifierValidator(),
        PunctuationValidator(":"),
        TypeValidator(),
        PunctuationValidator(";")
    ))

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].type == TokenType.KEYWORD &&
                statement[0].value.equals("let", ignoreCase = true) &&
                statement[4].value != "="
    }

    /*override fun analyzeStatement(statement: List<Token>): ValidationResult {
        return AnalyzeStatementService.analyzeStatement(statement, pattern)
    }*/

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(statement[1].value,
            Range(statement[1].range.start, statement[1].range.end))
        val range = Range(statement[0].range.start, statement[4].range.end)

        return VariableDeclarator(identifier, detectType(statement[3]), range)
    }

    override fun getPattern(): StatementPattern = pattern

    fun detectType(token: Token): Type {
        return when (token.type) {
            TokenType.NUMBER -> Type.NUMBER
            TokenType.STRING -> Type.STRING
            else -> throw IllegalArgumentException("Unsupported token type: ${token.type}")
        }
    }
}