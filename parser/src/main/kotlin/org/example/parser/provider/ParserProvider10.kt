package org.example.parser.provider

import org.example.ast.statements.VariableDeclarator
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.VariableStatementFactory
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser
import org.example.parser.validators.NumberValidator
import org.example.parser.validators.StringValidator
import org.example.parser.validators.SymbolValidator

class ParserProvider10 : ParserProvider {

    override fun provide(tokenBuffer: TokenBuffer): Parser {
        val keywordFactoryMap = createKeywordFactoryMap()

        val keywords = setOf("let")
        val types = setOf("Number", "String")
        val expressions = listOf(NumberValidator(), SymbolValidator(), StringValidator())

        val parsers = listOf<StatementParser>(
            VariableAssignationParser(expressions),
            VariableDeclarationParser(keywordFactoryMap, keywords, types, expressions),
            PrintParser(expressions)
        )

        val parser = Parser(parsers, tokenBuffer)
        return parser
    }

    private fun createKeywordFactoryMap(): Map<String, VariableStatementFactory> {
        return mapOf(
            "let" to { symbol, type, range, optionalExpr ->
                VariableDeclarator(symbol, type, range, optionalExpr)
            }
        )
    }
}
