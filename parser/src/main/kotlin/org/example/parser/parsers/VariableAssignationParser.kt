package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.token.Token
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

    override fun buildAST(statementBuffer: List<Token>): ASTNode {
        val symbol = SymbolExpression(
            statementBuffer[idPos].value,
            Position(statementBuffer[idPos].position.line, statementBuffer[idPos].position.column)
        )
        val range = Range(
            Position(statementBuffer[0].position.line, statementBuffer[0].position.column),
            Position(statementBuffer[statementBuffer.size - 1].position.line, statementBuffer[statementBuffer.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statementBuffer, equalsPos + 1, statementBuffer.size - 1)

        return VariableAssigner(symbol, expression, range)
    }

    override fun getPattern(): StatementPattern = pattern
}
