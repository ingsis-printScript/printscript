package org.example.parser.parsers

import org.example.common.Position
import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.enums.Type
import org.example.parser.exceptions.SyntaxException
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.TypeValidator


class VariableDeclarationParser : StatementParser {

    private val pattern = StatementPattern(listOf(
        KeywordValidator("let"), //mmmm hardcodeado
        IdentifierValidator(),
        PunctuationValidator(":"),
        TypeValidator(),
        PunctuationValidator(";")
    ))

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].type == TokenType.KEYWORD &&
                statement[0].value.equals("let", ignoreCase = true) &&
                statement[4].value != "="
    }

    /*override fun analyzeStatement(statement: List<Token>): ValidationResult {
        return AnalyzeStatementService.analyzeStatement(statement, pattern)
    }*/

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(statement[1].value,
            Position(statement[1].position.line, statement[1].position.column))
        val range = Range(
            Position(statement[0].position.line, statement[0].position.column),
            Position(statement[4].position.line, statement[4].position.column))


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
