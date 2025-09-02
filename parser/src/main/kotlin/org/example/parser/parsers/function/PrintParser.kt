package org.example.parser.parsers.function

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.StatementPattern
import org.example.parser.validators.ArgumentValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.expressionbuilder.ExpressionBuilder
import org.example.parser.validators.SymbolValidator

class PrintParser : StatementParser {
    private val pattern = StatementPattern(
        listOf(
            SymbolValidator(),
            ArgumentValidator(),
            PunctuationValidator(";")
        )
    )
    private val idPos = 0
    private val leftParenPos = 1

    override fun canParse(statement: List<Token>): Boolean {
        return statement.size > leftParenPos &&
            statement[idPos].type == TokenType.SYMBOL && // TODO(CHECK SYMBOL)
            statement[leftParenPos].type == TokenType.PUNCTUATION &&
            statement[leftParenPos].value == "("
    }

    override fun buildAST(statement: List<Token>): ASTNode {
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column),
            Position(statement[statement.size - 1].position.line, statement[statement.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statement, leftParenPos + 1, statement.size - 2)

        return PrintFunction(expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}