package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.ast.statements.VariableDeclarator
import org.example.common.enums.Type
import org.example.parser.TokenBuffer
import org.example.token.Token
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

        return VariableDeclarator(symbol, detectType(statementBuffer[3]), range, expression)
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
