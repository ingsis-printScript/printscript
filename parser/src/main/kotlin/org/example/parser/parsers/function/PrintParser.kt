package org.example.parser.parsers.function

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.IdentifierExpression
import org.example.ast.statements.functions.PrintFunction
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.StatementPattern
import org.example.parser.validators.ArgumentValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.parsers.ExpressionBuilder

class PrintParser : StatementParser {
    private val pattern = StatementPattern(
        listOf(
            IdentifierValidator(),
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
        val identifier = IdentifierExpression(
            statement[idPos].value,
            Position(statement[idPos].position.line, statement[idPos].position.column)
        )
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column),
            Position(statement[statement.size - 1].position.line, statement[statement.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        // TODO revisar que en index 2 no este ")"? -> caso para empty params
        val expression = expressionBuilder.buildExpression(statement, leftParenPos + 1, statement.size - 2)

        return PrintFunction(identifier, expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}