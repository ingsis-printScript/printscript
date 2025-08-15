package org.example.parser.parsers

import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.TypeValidator
import org.example.parser.ValidationResult

class VariableDeclarationAssignationParser: StatementParser {

    private val pattern = StatementPattern(listOf(
        KeywordValidator("let"),
        IdentifierValidator(),
        PunctuationValidator(":"),
        TypeValidator(),
        PunctuationValidator("="),
        ExpressionValidator(),
        PunctuationValidator(";")
    ))

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].kind == TokenType.KEYWORD &&
                statement[0].name.equals("let", ignoreCase = true) &&
                statement[1].kind == TokenType.SYMBOL &&
                statement[2].kind == TokenType.PUNCTUATION &&
                statement[2].name == ":" &&
                statement[4].kind == TokenType.PUNCTUATION &&
                statement[4].name == "="
    }

    /*override fun analyzeStatement(statement: List<Token>): ValidationResult {
        return AnalyzeStatementService.analyzeStatement(statement, pattern)
    }*/

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(statement[1].name, statement[3].name,
            Range(statement[1].range.start, statement[1].range.end))
        val range = Range(statement[0].range.start, statement[6].range.end)

        return VariableDeclarator(identifier, range)
    }

    override fun getPattern(): StatementPattern = pattern
}