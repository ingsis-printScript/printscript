package org.example.parser.provider

import org.example.ast.expressions.Expression
import org.example.ast.expressions.OptionalExpression
import org.example.ast.expressions.ReadEnvExpression
import org.example.ast.expressions.ReadInputExpression
import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.common.Range
import org.example.common.enums.Type
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.VariableStatementFactory
import org.example.parser.parsers.ConditionParser
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser
import org.example.parser.validators.BooleanValidator
import org.example.parser.validators.NumberValidator
import org.example.parser.validators.StringValidator
import org.example.parser.validators.SymbolValidator

class ParserProvider11 : ParserProvider {

    override fun provide(tokenBuffer: TokenBuffer): Parser {
        val keywordFactoryMap = createKeywordFactoryMap()

        val declarators = setOf("let", "const")
        val types = setOf(Type.NUMBER, Type.STRING, Type.BOOLEAN)
        val expressionElements = listOf(
            NumberValidator(),
            SymbolValidator(),
            StringValidator(),
            BooleanValidator()
        )

        val keywordMap: Map<String, (OptionalExpression, Range) -> Expression> = mapOf(
            "readinput" to { opt, range -> ReadInputExpression(opt, range) },
            "readenv" to { opt, range -> ReadEnvExpression(opt, range) }
        )

        val commonParsers = listOf<StatementParser>(
            VariableAssignationParser(expressionElements, keywordMap),
            VariableDeclarationParser(keywordFactoryMap, declarators, types, expressionElements, keywordMap),
            PrintParser(expressionElements, keywordMap)
        )

        // check
        val conditionParser = ConditionParser(commonParsers, expressionElements, keywordMap)

        val parsers = commonParsers + conditionParser

        return Parser(parsers, tokenBuffer)
    }

    private fun createKeywordFactoryMap(): Map<String, VariableStatementFactory> {
        return mapOf(
            "let" to { symbol, type, range, optionalExpr ->
                VariableDeclarator(symbol, type, range, optionalExpr)
            },
            "const" to { symbol, type, range, optionalExpr ->
                VariableImmutableDeclarator(symbol, type, range, optionalExpr)
            }
        )
    }
}
