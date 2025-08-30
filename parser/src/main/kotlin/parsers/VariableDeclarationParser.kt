package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.ast.ASTNode
import org.example.ast.expressions.IdentifierExpression
import org.example.ast.statements.VariableDeclarator
import org.example.common.enums.Type
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.TypeValidator

class VariableDeclarationParser : StatementParser {

    private val pattern = StatementPattern(
        listOf(
            KeywordValidator(),
            IdentifierValidator(),
            PunctuationValidator(":"),
            TypeValidator(),
            PunctuationValidator(";")
        )
    )

    private val letPos = 0
    private val equalsPos = 4

    override fun canParse(statement: List<Token>): Boolean {
        return statement.size > equalsPos &&
            statement[letPos].type == TokenType.KEYWORD &&
            statement[letPos].value.equals("let", ignoreCase = true) &&
            statement[equalsPos].value != "="
    }

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(
            statement[1].value,
            Position(statement[1].position.line, statement[1].position.column)
        )
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column),
            Position(statement[4].position.line, statement[4].position.column)
        )

        return VariableDeclarator(identifier, detectType(statement[3]), range)
    }

    override fun getPattern(): StatementPattern = pattern

    private fun detectType(token: Token): Type {
        return when (token.value.lowercase()) {
            "number" -> Type.NUMBER
            "string" -> Type.STRING
            else -> throw SyntaxException("Unsupported token type: ${token.type}")
        }
    }
}
