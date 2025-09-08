package org.example.parser.parsers.function

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.statements.functions.PrintFunction
import org.example.parser.TokenBuffer
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.StatementPattern
import org.example.parser.validators.PunctuationValidator
import org.example.parser.expressionbuilder.ExpressionBuilder
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.SymbolValidator

class PrintParser : StatementParser {
    private val pattern = StatementPattern(
        listOf(
            SymbolValidator(),
            PunctuationValidator("("),
            ExpressionValidator(),
            PunctuationValidator(")"),
            PunctuationValidator(";")
        )
    )
    private val leftParenPos = 1

    override fun buildAST(statementBuffer: TokenBuffer): ASTNode {
        val range = Range(
            Position(statementBuffer[0].position.line, statementBuffer[0].position.column),
            Position(statementBuffer[statementBuffer.size - 1].position.line, statementBuffer[statementBuffer.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statementBuffer, leftParenPos + 1, statementBuffer.size - 2)

        return PrintFunction(expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}