package org.example.parser.parsers

import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.IdentifierValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.TypeValidator
import org.example.parser.validators.ValidationResult

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

    override fun analyzeStatement(statement: List<Token>): ValidationResult {
        if (statement.size < pattern.validators.size) {
            return ValidationResult.Error(
                "Expected ${pattern.validators.size} tokens, found ${statement.size}", 0)
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
        val identifier = (statement[1] as IdentifierToken).name
        val type = (statement[3] as TypeToken).kind.name
        val range = Range(/* calcular desde tokens */) //cada token tiene su range

        return VariableDeclarator(type, range, identifier)
    } //Hacer hijos?

    override fun getPattern(): StatementPattern = pattern
}