package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableAssigner
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.PunctuationValidator
import parsers.ExpressionBuilder

class VariableAssignationParser: StatementParser {

    private val pattern = StatementPattern(
        listOf(
            IdentifierValidator(),
            PunctuationValidator("="),
            ExpressionValidator(),
            PunctuationValidator(";")
        )
    )

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].type == TokenType.SYMBOL &&
                statement[1].type == TokenType.PUNCTUATION &&
                statement[1].value == "="
    }


    /*override fun analyzeStatement(statement: List<Token>): ValidationResult {
        return AnalyzeStatementService.analyzeStatement(statement, pattern)
    }*/

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(statement[0].value,
            Position(statement[0].position.line, statement[0].position.column))
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column), Position(statement[statement.size - 1].position.line, statement[statement.size - 1].position.column))

        val expressionBuilder = ExpressionBuilder() //es re feo que me tengo que crear un expression builder
        val expression = expressionBuilder.buildExpression(statement, 2, statement.size - 1)

        return VariableAssigner(identifier, expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}