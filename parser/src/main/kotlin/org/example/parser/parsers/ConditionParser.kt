package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.statements.Condition
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.TokenType
import org.example.parser.parsers.builders.block.BlockBuilder
import org.example.parser.parsers.builders.expression.ExpressionBuilder
import org.example.parser.validators.*
import org.example.token.Token

class ConditionParser(
    statementParsers: List<StatementParser>,
    expressionElements: List<TokenValidator>,
    private val keywordMap: Map<String, (OptionalExpression, Range) -> Expression>
) : StatementParser {
    private val blockBuilder = BlockBuilder(statementParsers)

    private val patterns = listOf(
        StatementPattern(
            listOf(
                KeywordValidator(setOf("if")),
                PunctuationValidator("("),
                ExpressionValidator(expressionElements), //check
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
                ExpressionValidator(expressionElements),
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

        val expressionBuilder = ExpressionBuilder(keywordMap)
        val condition = expressionBuilder.buildExpression(statements, 2, endOfCondition(statements))
        // endOfCondition or endOfCondition-1?... Creo que incluyo al ')'
        // todo: OptionalExpression -> error... or leave up to interpreter?

        val divider = endOfBlock(statements)
        val ifBlock = blockBuilder.build(statements.subList(5, divider))

        if (noElseBlock(divider, statements)) {
            return Condition(condition, ifBlock, null, range)
        } else {
            val elseBlock = blockBuilder.build(statements.subList(divider + 3, statements.size - 1))
            return Condition(condition, ifBlock, elseBlock, range)
        }
    }

    private fun noElseBlock(divider: Int, statements: List<Token>) =
        divider + 1 >= statements.size - 1

    private fun endOfBlock(statements: List<Token>) =
        statements.indexOfFirst { it.type == TokenType.PUNCTUATION && it.value == "}" }

    private fun endOfCondition(statements: List<Token>) =
        statements.indexOfFirst { it.type == TokenType.PUNCTUATION && it.value == ")" }

    override fun getPatterns(): List<StatementPattern> = patterns
}
