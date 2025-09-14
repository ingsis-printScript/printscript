package org.example.parser.provider

import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.VariableStatementFactory
import org.example.parser.parsers.ConditionParser
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser
import org.example.parser.validators.NumberValidator
import org.example.parser.validators.StringValidator
import org.example.parser.validators.SymbolValidator
import org.example.parser.validators.BooleanValidator

class ParserProvider11 : ParserProvider {

    override fun provide(tokenBuffer: TokenBuffer): Parser {
        val keywordFactoryMap = createKeywordFactoryMap()

        val keywords = setOf("let", "const")
        val types = setOf("Number", "String")
        val expressions = listOf(
            NumberValidator(),
            SymbolValidator(),
            StringValidator(),
            BooleanValidator(),
        )

        val commonParsers = listOf<StatementParser>(
            VariableAssignationParser(expressions),
            VariableDeclarationParser(keywordFactoryMap, keywords, types, expressions),
            PrintParser(expressions)
        )

        val conditionParser = ConditionParser(commonParsers)

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