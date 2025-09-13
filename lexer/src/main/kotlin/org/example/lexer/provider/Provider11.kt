package org.example.lexer.provider

import org.example.lexer.Lexer
import org.example.lexer.constructors.*

class Provider11 : Provider {
    override fun provide(reader: Iterator<String>): Lexer {
        val keywords = setOf("let, const", "if", "else")
        val operators = setOf("+", "-", "*", "/")
        val punctuations = setOf(":", ";", ",", "(", ")", "{", "}", "=")

        val constructors = mutableListOf(
            NumberTokenConstructor(),
            OperatorTokenConstructor(operators),
            PunctuationTokenConstructor(punctuations),
            StringTokenConstructor(),
            SymbolTokenConstructor(),
            BooleanTokenConstructor()
        )
        val keywordConstructor = KeywordTokenConstructor(keywords)
        val whiteSpaces = listOf(' ', '\t', '\n')

        val lexer = Lexer(reader, constructors, keywordConstructor, whiteSpaces)

        return lexer
    }
}