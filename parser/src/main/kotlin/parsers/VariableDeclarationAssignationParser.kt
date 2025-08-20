package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.enums.Type
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.TypeValidator
import parsers.ExpressionBuilder

class VariableDeclarationAssignationParser: StatementParser {

    private val pattern = StatementPattern(listOf(
        KeywordValidator("let"),
        IdentifierValidator(),
        PunctuationValidator(":"),
        TypeValidator(),
        PunctuationValidator("="),
        ExpressionValidator(),
        PunctuationValidator(";")
    ))

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].type == TokenType.KEYWORD &&
                statement[0].value.equals("let", ignoreCase = true) &&
                statement[1].type == TokenType.SYMBOL &&
                statement[4].type == TokenType.PUNCTUATION &&
                statement[4].value == "="
    }

    /*override fun analyzeStatement(statement: List<Token>): ValidationResult {
        return AnalyzeStatementService.analyzeStatement(statement, pattern)
    }*/

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(statement[1].value,
            Position(statement[1].position.line, statement[1].position.column))
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column), Position(statement[statement.size - 1].position.line, statement[statement.size - 1].position.column))


        val expressionBuilder = ExpressionBuilder() //es re feo que me tengo que crear un expression builder
        val expression = expressionBuilder.buildExpression(statement, 5, statement.size - 1)

        return VariableDeclarator(identifier, detectType(statement[3]), range, expression)
    }

    fun detectType(token: Token): Type {
        return when (token.type) {
            TokenType.NUMBER -> Type.NUMBER
            TokenType.STRING -> Type.STRING
            else -> throw IllegalArgumentException("Unsupported token type: ${token.type}")
        }
    }

    override fun getPattern(): StatementPattern = pattern
}