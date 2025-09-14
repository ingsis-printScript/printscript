package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.ast.expressions.BooleanExpression
import org.example.ast.statements.Condition
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.TokenType
import org.example.parser.parsers.builders.block.BlockBuilder
import org.example.parser.validators.BlockValidator
import org.example.parser.validators.BooleanValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.token.Token

class ConditionParser(
    private val statementParsers: List<StatementParser>
) : StatementParser {
    private val blockBuilder = BlockBuilder(statementParsers)

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
        val range = Range(
            Position(statements[0].position.line, statements[0].position.column),
            Position(statements[statements.size - 1].position.line, statements[statements.size - 1].position.column)
        )

        val condition = statements[2]
        val boolean = BooleanExpression(condition.value, condition.position)

        val divider = endOfBlock(statements)
        val ifBlock = blockBuilder.build(statements.subList(5, divider))

        if (noElseBlock(divider, statements)) {
            return Condition(boolean, ifBlock, null, range)
        } else {
            val elseBlock = blockBuilder.build(statements.subList(divider + 3, statements.size - 1))
            return Condition(boolean, ifBlock, elseBlock, range)
        }
    }

    private fun noElseBlock(divider: Int, statements: List<Token>) =
        divider + 1 >= statements.size - 1

    private fun endOfBlock(statements: List<Token>) =
        statements.indexOfFirst { it.type == TokenType.PUNCTUATION && it.value == "}" }

    override fun getPatterns(): List<StatementPattern> = patterns
}
