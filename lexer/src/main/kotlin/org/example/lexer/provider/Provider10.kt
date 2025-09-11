package org.example.lexer.provider

import org.example.common.tokens.Keyword
import org.example.lexer.Lexer
import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.NumberTokenConstructor
import org.example.lexer.constructors.OperatorTokenConstructor
import org.example.lexer.constructors.PunctuationTokenConstructor
import org.example.lexer.constructors.StringTokenConstructor
import org.example.lexer.constructors.SymbolTokenConstructor
import org.example.lexer.constructors.TokenConstructor

class Provider10: Provider {

    override fun provide(reader: Iterator<String>): Lexer {
        val keywords = setOf("let", "const")
        val operators = setOf("+", "-", "*", "/")
        val punctuations = setOf(";", ",", "(", ")", "{", "}", "=")


        val constructors = mutableListOf<TokenConstructor>(NumberTokenConstructor(), OperatorTokenConstructor(stringToOperator(operators)),
            PunctuationTokenConstructor(stringToPunctuation(punctuations)), StringTokenConstructor(),
            SymbolTokenConstructor()
        )
        val keywordConstructor = KeywordTokenConstructor(stringToKeyword(keywords))
        val whiteSpaces = listOf(' ', '\t', '\n')

        val lexer = Lexer(reader, constructors, keywordConstructor, whiteSpaces)

        return lexer
    }


    private fun stringToPunctuation(punctuations: Set<String>): Set<org.example.common.tokens.Punctuation> {
        return punctuations.map { object : org.example.common.tokens.Punctuation {
            override val value: String = it
        } }.toSet()
    }

    private fun stringToOperator(operators: Set<String>): Set<org.example.common.tokens.Operator> {
        return operators.map { object : org.example.common.tokens.Operator {
            override val value: String = it
        } }.toSet()
    }

    private fun stringToKeyword(keywords: Set<String>): Set<Keyword> {
        return keywords.map { object : Keyword {
            override val value: String = it
        } }.toSet()
    }
}