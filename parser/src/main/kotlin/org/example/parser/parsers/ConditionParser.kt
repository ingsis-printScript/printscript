package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.parser.validators.*
import org.example.token.Token

class ConditionParser(
    statementParsers: List<StatementParser>
) : StatementParser {
    private val patterns = listOf(
        StatementPattern(
            listOf(
                KeywordValidator(setOf("if")),
                PunctuationValidator("("),
                BooleanValidator(),
                PunctuationValidator(")"),
                PunctuationValidator("{"),
                BlockValidator(statementParsers),
                PunctuationValidator("}")
            )
        ),
        StatementPattern(
            listOf(
                KeywordValidator(setOf("if")),
                PunctuationValidator("("),
                BooleanValidator(),
                PunctuationValidator(")"),
                PunctuationValidator("{"),
                BlockValidator(statementParsers),
                PunctuationValidator("}"),
                KeywordValidator(setOf("else")),
                PunctuationValidator("{"),
                BlockValidator(statementParsers),
                PunctuationValidator("}")
            )
        )
    )

    override fun buildAST(statements: List<Token>): ASTNode {
        TODO("Not yet implemented")
    }

    override fun getPatterns(): List<StatementPattern> = patterns
}