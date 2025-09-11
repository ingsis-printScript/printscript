package org.example.cli.factory

import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.TokenConstructor
import org.example.parser.parsers.StatementParser

class PrintScript11Setup : SetupData {
    override fun constructors(): List<TokenConstructor> {
        TODO("Not yet implemented")
    }

    override fun keywordConstructor(): KeywordTokenConstructor {
        TODO("Not yet implemented")
    }

    override fun whitespaces(): List<Char> {
        TODO("Not yet implemented")
    }

    override fun statementParsers(): List<StatementParser> {
        TODO("Not yet implemented")
    }
}
