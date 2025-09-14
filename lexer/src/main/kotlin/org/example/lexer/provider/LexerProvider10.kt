package org.example.lexer.provider

import org.example.lexer.Lexer
import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.NumberTokenConstructor
import org.example.lexer.constructors.OperatorTokenConstructor
import org.example.lexer.constructors.PunctuationTokenConstructor
import org.example.lexer.constructors.StringTokenConstructor
import org.example.lexer.constructors.SymbolTokenConstructor

class LexerProvider10 : LexerProvider {

    override fun provide(reader: Iterator<String>): Lexer {
        val keywords = setOf("let")
        val operators = setOf("+", "-", "*", "/")
        val punctuations = setOf(":", ";", ",", "(", ")", "=")

        val constructors = mutableListOf(
            NumberTokenConstructor(),
            OperatorTokenConstructor(operators),
            PunctuationTokenConstructor(punctuations),
            StringTokenConstructor(),
            SymbolTokenConstructor()
        )
        val keywordConstructor = KeywordTokenConstructor(keywords)
        val whiteSpaces = listOf(' ', '\t', '\n')

        val lexer = Lexer(reader, constructors, keywordConstructor, whiteSpaces)

        return lexer
    }
}
