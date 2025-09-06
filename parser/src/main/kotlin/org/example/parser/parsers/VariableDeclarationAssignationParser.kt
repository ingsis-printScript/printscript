package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableDeclarator
import org.example.common.enums.Type
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.expressionbuilder.ExpressionBuilder
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.SymbolValidator
import org.example.parser.validators.TypeValidator

class VariableDeclarationAssignationParser : StatementParser {

    private val pattern = StatementPattern(
        listOf(
            KeywordValidator(),
            SymbolValidator(),
            PunctuationValidator(":"),
            TypeValidator(),
            PunctuationValidator("="),
            ExpressionValidator(),
            PunctuationValidator(";")
        )
    )

    private val idPos = 1
    private val equalsPos = 4

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

        return VariableDeclarator(symbol, detectType(statement[3]), range, expression)
    }

    private fun detectType(token: Token): Type {
        return when (token.value.lowercase()) {
            "number" -> Type.NUMBER
            "string" -> Type.STRING
            else -> throw SyntaxException("Unsupported token type: ${token.type}")
        }
    }

    override fun getPattern(): StatementPattern = pattern
}
