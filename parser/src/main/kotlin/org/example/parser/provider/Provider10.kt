package org.example.parser.provider

import org.example.ast.statements.VariableDeclarator
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.VariableStatementFactory
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser

class Provider10 : Provider {

    override fun provide(tokenBuffer: TokenBuffer): Parser {
        val keywordFactoryMap = createKeywordFactoryMap()

        val parsers = listOf<StatementParser>(
            VariableAssignationParser(),
            VariableDeclarationParser(keywordFactoryMap),
            PrintParser()
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
