package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.expressionbuilder.ExpressionBuilder
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.SymbolValidator

class VariableAssignationParser : StatementParser {

    private val pattern = StatementPattern(
        listOf(
            SymbolValidator(),
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
        val symbol = SymbolExpression(
            statement[idPos].value,
            Position(statement[idPos].position.line, statement[idPos].position.column)
        )
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column),
            Position(statement[statement.size - 1].position.line, statement[statement.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statement, equalsPos + 1, statement.size - 1)

        return VariableAssigner(symbol, expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}
