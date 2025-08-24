package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.enums.Type
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.TypeValidator
import parsers.ExpressionBuilder

class VariableDeclarationAssignationParser : StatementParser {

    private val pattern = StatementPattern(
        listOf(
            KeywordValidator("let"),
            IdentifierValidator(),
            PunctuationValidator(":"),
            TypeValidator(),
            PunctuationValidator("="),
            ExpressionValidator(),
            PunctuationValidator(";")
        )
    )

    private val letPos = 0
    private val idPos = 1
    private val equalsPos = 4

    override fun canParse(statement: List<Token>): Boolean { // todo Index vs contains, check size until max
        return statement.size > equalsPos &&
            statement[letPos].type == TokenType.KEYWORD &&
            statement[letPos].value.equals("let", ignoreCase = true) &&
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

        return VariableDeclarator(identifier, detectType(statement[3]), range, expression)
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
