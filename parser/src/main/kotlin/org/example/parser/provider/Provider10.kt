package org.example.parser.provider

import org.example.ast.statements.VariableDeclarator
import org.example.ast.statements.VariableImmutableDeclarator
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.VariableStatementFactory
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser

class Provider10: Provider {

    override fun provide(tokenBuffer: TokenBuffer): Parser {
        val keywordFactoryMap = createKeywordFactoryMap()

        val parsers = listOf<StatementParser>(VariableAssignationParser(),
            VariableDeclarationParser(keywordFactoryMap), PrintParser()
        )

        val parser = Parser(parsers, tokenBuffer)
        return parser
    }

    private fun createKeywordFactoryMap(): Map<String, VariableStatementFactory> {
        return mapOf(
            "var" to { symbol, type, range, value ->
                VariableDeclarator(symbol, type, range, value)
            },
            "val" to { symbol, type, range, value ->
                VariableImmutableDeclarator(symbol, type, range, value)
            }
        )
    }
}