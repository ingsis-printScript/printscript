package org.example.cli.factory

import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.NumberTokenConstructor
import org.example.lexer.constructors.OperatorTokenConstructor
import org.example.lexer.constructors.PunctuationTokenConstructor
import org.example.lexer.constructors.StringTokenConstructor
import org.example.lexer.constructors.SymbolTokenConstructor
import org.example.lexer.constructors.TokenConstructor
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser

class PrintScript10Setup : SetupData {

    override fun constructors(): List<TokenConstructor> {
        return listOf(
            NumberTokenConstructor(),
            OperatorTokenConstructor(setOf()),
            PunctuationTokenConstructor(setOf()),
            StringTokenConstructor(),
            SymbolTokenConstructor()
        )
    }

    override fun keywordConstructor(): KeywordTokenConstructor {
        return KeywordTokenConstructor(setOf())
    }

    override fun whitespaces(): List<Char> {
        return listOf(' ', '\t', '\n')
    }

    override fun statementParsers(): List<StatementParser> {
        return listOf(
            VariableAssignationParser(),
            VariableDeclarationParser(mapOf()),
            PrintParser()
        )
    }
}
