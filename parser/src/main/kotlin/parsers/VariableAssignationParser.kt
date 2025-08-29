package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableAssigner
import org.example.common.tokens.Token
import org.example.common.enums.TokenType
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.PunctuationValidator
import parsers.ExpressionBuilder

class VariableAssignationParser : StatementParser {

    private val pattern = StatementPattern(
        listOf(
            IdentifierValidator(),
            PunctuationValidator("="),
            ExpressionValidator(),
            PunctuationValidator(";")
        )
    )

    private val idPos = 0
    private val equalsPos = 1

    override fun canParse(statement: List<Token>): Boolean {
        return statement.size > equalsPos &&
            statement[idPos].type == TokenType.SYMBOL &&
            statement[equalsPos].type == TokenType.PUNCTUATION &&
            statement[equalsPos].value == "="
    }

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(
            statement[idPos].value,
            Position(statement[idPos].position.line, statement[idPos].position.column)
        )
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column),
            Position(statement[statement.size - 1].position.line, statement[statement.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statement, equalsPos + 1, statement.size - 1)

        return VariableAssigner(identifier, expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}
