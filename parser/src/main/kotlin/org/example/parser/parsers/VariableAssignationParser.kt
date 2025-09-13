package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableAssigner
import org.example.common.Position
import org.example.common.Range
import org.example.parser.parsers.builders.expression.ExpressionBuilder
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.SymbolValidator
import org.example.parser.validators.TokenValidator
import org.example.token.Token

class VariableAssignationParser(
    expressionValidators: List<TokenValidator>
) : StatementParser {

    private val idPos = 0
    private val equalsPos = 1

    private val patterns = listOf(
        StatementPattern(
            listOf(
                SymbolValidator(),
                PunctuationValidator("="),
                ExpressionValidator(expressionValidators),
                PunctuationValidator(";")
            )
        )
    )

    override fun buildAST(statements: List<Token>): ASTNode {
        val symbol = SymbolExpression(
            statements[idPos].value,
            Position(statements[idPos].position.line, statements[idPos].position.column)
        )
        val range = Range(
            Position(statements[0].position.line, statements[0].position.column),
            Position(statements[statements.size - 1].position.line, statements[statements.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statements, equalsPos + 1, statements.size - 1)

        return VariableAssigner(symbol, expression, range)
    }

    override fun getPatterns(): List<StatementPattern> = patterns
}
