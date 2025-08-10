package org.example.parser.parsers

import org.example.common.Range
import org.example.common.ast.ASTNode
import org.example.common.ast.statements.VariableDeclarator
import org.example.common.tokens.IdentifierToken
import org.example.common.tokens.KeywordToken
import org.example.common.tokens.Token
import org.example.common.tokens.TypeToken
import org.example.common.tokens.enums.Keywords
import org.example.common.tokens.enums.Punctuation
import org.example.parser.validators.*

class VariableDeclarationOnlyParser : StatementParser {

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

        pattern.validators.forEachIndexed { index, validator ->
            val result = validator.validate(statement[index], index)
            if (result is ValidationResult.Error) {
                return result
            }
        }

        return ValidationResult.Success
    }

    override fun buildAST(statement: List<Token>): ASTNode {
        val identifier = (statement[1] as IdentifierToken).name
        val type = (statement[3] as TypeToken).kind.name
        val range = Range(/* calcular desde tokens */)

        return VariableDeclarator(type, range, identifier)
    }

    override fun getPattern(): StatementPattern = pattern
}