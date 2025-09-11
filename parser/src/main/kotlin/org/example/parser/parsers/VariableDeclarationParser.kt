package org.example.parser.parsers

import org.example.ast.ASTNode
import org.example.ast.expressions.SymbolExpression
import org.example.common.Position
import org.example.common.Range
import org.example.common.enums.Type
import org.example.parser.VariableStatementFactory
import org.example.parser.exceptions.SyntaxException
import org.example.parser.parsers.expressionbuilder.ExpressionBuilder
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.SymbolValidator
import org.example.parser.validators.TypeValidator
import org.example.token.Token

private const val ID_POS: Int = 1
private const val EQUALS_POS = 4

class VariableDeclarationParser(
    private val keywordFactoryMap: Map<String, VariableStatementFactory>
) : StatementParser {

    private val patterns = listOf(
        StatementPattern(
            listOf(
                KeywordValidator(),
                SymbolValidator(),
                PunctuationValidator(":"),
                TypeValidator(),
                PunctuationValidator("="),
                ExpressionValidator(),
                PunctuationValidator(";")
            )
        ),
        StatementPattern(
            listOf(
                KeywordValidator(),
                SymbolValidator(),
                PunctuationValidator(":"),
                TypeValidator(),
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
            Position(statements[ID_POS].position.line, statements[ID_POS].position.column)
        )
        val range = Range(
            Position(statements[0].position.line, statements[0].position.column),
            Position(statements[statements.size - 1].position.line, statements[statements.size - 1].position.column)
        )

        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.buildExpression(statements, EQUALS_POS + 1, statements.size - 1)

        return factory(symbol, detectType(statements[3]), range, expression)
    }

    private fun detectType(token: Token): Type {
        return when (token.value.lowercase()) {
            "number" -> Type.NUMBER
            "string" -> Type.STRING
            else -> throw SyntaxException("Unsupported token type: ${token.type}")
        }
    }

    override fun getPatterns(): List<StatementPattern> = patterns
}
