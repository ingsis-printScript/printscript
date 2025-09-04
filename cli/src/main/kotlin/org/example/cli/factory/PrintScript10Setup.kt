package org.example.cli.factory

import org.example.lexer.constructors.*
import org.example.parser.parsers.StatementParser
import org.example.parser.parsers.VariableAssignationParser
import org.example.parser.parsers.VariableDeclarationAssignationParser
import org.example.parser.parsers.VariableDeclarationParser
import org.example.parser.parsers.function.PrintParser

class PrintScript10Setup : SetupData {

    override fun constructors() : List<TokenConstructor> {
        return listOf(
            NumberTokenConstructor(),
            OperatorTokenConstructor(),
            PunctuationTokenConstructor(),
            StringTokenConstructor(),
            SymbolTokenConstructor()
        );
    }

    override fun keywordConstructor() : KeywordTokenConstructor {
        return KeywordTokenConstructor();
    }

    override fun whitespaces() : List<Char> {
        return listOf(' ', '\t', '\n')
    }

    override fun statementParsers() : List<StatementParser> {
        return listOf(
            VariableAssignationParser(),
            VariableDeclarationParser(),
            VariableDeclarationAssignationParser(),
            PrintParser()
        )
    }
}