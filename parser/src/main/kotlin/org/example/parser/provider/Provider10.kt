package org.example.parser.provider

import org.example.ast.statements.VariableDeclarator
import org.example.parser.Parser
import org.example.parser.TokenBuffer
import org.example.parser.VariableStatementFactory
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.StatementPattern
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser
import org.example.parser.validators.ExpressionValidator
import org.example.parser.validators.KeywordValidator
import org.example.parser.validators.NumberValidator
import org.example.parser.validators.PunctuationValidator
import org.example.parser.validators.StringValidator
import org.example.parser.validators.SymbolValidator
import org.example.parser.validators.TypeValidator

class Provider10 : Provider {

    override fun provide(tokenBuffer: TokenBuffer): Parser {
        val keywordFactoryMap = createKeywordFactoryMap()

        val expressionValidators = listOf(NumberValidator(), SymbolValidator(), StringValidator())
        val types = setOf("Number", "String")
        val keywords = setOf("let")

        val parsers = listOf<StatementParser>(
            VariableAssignationParser(expressionValidators),
            VariableDeclarationParser(keywordFactoryMap, keywords, types, expressionValidators),
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
