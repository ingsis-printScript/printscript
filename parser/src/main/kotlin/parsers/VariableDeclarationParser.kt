package org.example.parser.parsers

import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.Token
import org.example.common.tokens.enums.Keywords
import org.example.common.tokens.enums.Punctuation
import org.example.parser.validators.*

class VariableDeclarationParser : StatementParser {

    private val pattern = StatementPattern(listOf(
        KeywordValidator("let"),
        IdentifierValidator(),
        PunctuationValidator(Punctuation.COLON),
        TypeValidator(),
        PunctuationValidator(Punctuation.SEMICOLON)
    ))

    override fun canParse(statement: List<Token>): Boolean {
        return statement.isNotEmpty() &&
                statement[0] is KeywordToken &&
                (statement[0] as KeywordToken).kind == Keywords.LET
    }

    override fun analyzeStatement(statement: List<Token>): ValidationResult {
        if (statement.size != pattern.validators.size) {
            return ValidationResult.Error(
                "Expected ${pattern.validators.size} tokens, found ${statement.size}", 0)
        }

        // TODO(Fix para expressions... no sé cómo hacer porque validate recibe UN TOKEN.)
        // necesito un tipo composite...of sorts. VER

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