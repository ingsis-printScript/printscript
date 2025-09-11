package org.example.cli.factory

import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.TokenConstructor
import org.example.parser.parsers.StatementParser

interface SetupData {
    fun constructors(): List<TokenConstructor>
    fun keywordConstructor(): KeywordTokenConstructor; // TODO(inyectar en c/ impl qué keywords tomo)
    fun whitespaces(): List<Char>

    fun statementParsers(): List<StatementParser>

    // lo que necesite para interpreter
    // lo que necesite para formatter
    // lo que necesite para linter
}
