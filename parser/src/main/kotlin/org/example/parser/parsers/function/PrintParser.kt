package org.example.parser.parsers.function

import org.example.ast.ASTNode
import org.example.ast.statements.functions.PrintFunction
import org.example.common.Position
import org.example.common.Range
import org.example.parser.expressionbuilder.ExpressionBuilder
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.StatementPattern
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.SymbolValidator
import org.example.token.Token

class PrintParser : StatementParser {
    private val patterns = listOf(
        StatementPattern(
            listOf(
                SymbolValidator(),
                PunctuationValidator("("),
                ExpressionValidator(),
                PunctuationValidator(")"),
                PunctuationValidator(";")
            )
        )
    )
    private val leftParenPos = 1

    override fun buildAST(statements: List<Token>): ASTNode {
        val range = Range(
            Position(statements[0].position.line, statements[0].position.column),
            Position(statements[statements.size - 1].position.line, statements[statements.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statements, leftParenPos + 1, statements.size - 2)

        return PrintFunction(expression, range)
    }

    override fun getPatterns(): List<StatementPattern> = patterns
}
