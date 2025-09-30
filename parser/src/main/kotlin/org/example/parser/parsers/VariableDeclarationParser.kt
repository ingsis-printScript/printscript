package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.SymbolExpression
import org.example.common.Range
import org.example.common.enums.Type
import org.example.parser.VariableStatementFactory
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.builders.expression.ExpressionBuilder
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.SymbolValidator
import org.example.parser.validators.TokenValidator
import org.example.parser.validators.TypeValidator
import org.example.token.Token

private const val ID_POS: Int = 1
private const val EQUALS_POS = 4

class VariableDeclarationParser(
    private val keywordFactoryMap: Map<String, VariableStatementFactory>,
    private val expectedKeywords: Set<String>,
    private val expectedTypes: Set<Type>,
    private val expressionValidators: List<TokenValidator>,
    private val keywordMap: Map<String, (OptionalExpression, Range) -> Expression>
) : StatementParser {

    private val types = expectedTypes.map { it.name.lowercase() }.toSet()
    private val patterns = listOf(
        StatementPattern(
            listOf(
                KeywordValidator(expectedKeywords),
                SymbolValidator(),
                PunctuationValidator(":"),
                TypeValidator(types),
                PunctuationValidator("="),
                ExpressionValidator(expressionValidators),
                PunctuationValidator(";")
            )
        ),
        StatementPattern(
            listOf(
                KeywordValidator(expectedKeywords),
                SymbolValidator(),
                PunctuationValidator(":"),
                TypeValidator(types),
                PunctuationValidator(";")
            )
        )
    )

    override fun buildAST(statements: List<Token>): ASTNode {
        val keyword = statements[0].value.lowercase()
        val factory = keywordFactoryMap[keyword]
            ?: throw SyntaxException(
                "Unsupported variable declaration keyword: $keyword. " +
                    "Supported keywords: ${keywordFactoryMap.keys.joinToString(", ")}"
            )

        val symbol = SymbolExpression(
            statements[ID_POS].value,
            statements[ID_POS].position
        )
        val range = Range(
            statements[0].position,
            statements[statements.size - 1].position
        )

        val expressionBuilder = ExpressionBuilder(keywordMap)
        val expression = expressionBuilder.buildExpression(statements, EQUALS_POS + 1, statements.size - 1)

        return factory(symbol, detectType(statements[3]), range, expression)
    }

    private fun detectType(token: Token): Type {
        val type = token.value.lowercase()
        if (type !in types) {
            throw SyntaxException("Unsupported token type: ${token.type}")
        } else {
            return getCorrectType(type)
        }
    }

    private fun getCorrectType(type: String): Type {
        return expectedTypes.first { it.name.equals(type, ignoreCase = true) }
    }

    override fun getPatterns(): List<StatementPattern> = patterns
}
