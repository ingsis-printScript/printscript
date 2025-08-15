package org.example.parser.parsers

import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.expressions.IdentifierExpression
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.ValidationResult

class VariableAssignationParser: StatementParser {

    private val pattern = StatementPattern(
        listOf(
            IdentifierValidator(),
            PunctuationValidator("="),
            ExpressionValidator(),
            PunctuationValidator(";")
        )
    )

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0].kind == TokenType.SYMBOL &&
                statement[1].kind == TokenType.PUNCTUATION &&
                statement[1].name == "="
    }


    override fun analyzeStatement(statement: List<Token>): ValidationResult {
        if (statement.size < pattern.validators.size) {
            return ValidationResult.Error(
                "Expected at least ${pattern.validators.size} tokens, found ${statement.size}", 0)
        }

        var position = 0
        pattern.validators.forEachIndexed { _, validator ->
            if (statement.size <= position) {
                return ValidationResult.Error(
                    "Expected more tokens, found ${statement.size} at position $position",
                    position
                )
            }
            when (val result = validator.validate(statement, position)) {
                is ValidationResult.Error -> return result
                is ValidationResult.Success -> position += result.consumed
            }
        }

        return ValidationResult.Success(position)
    }

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = IdentifierExpression(statement[0].name, statement[0].name,
            Range(statement[0].range.start, statement[0].range.end))
        val range = Range(statement[0].range.start, statement[3].range.end)

        return VariableDeclarator(identifier, range, identifier)
    } //Hacer hijos?

    override fun getPattern(): StatementPattern = pattern
}