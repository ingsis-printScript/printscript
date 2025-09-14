package org.example.lexer.provider

import org.example.lexer.Lexer

sealed interface LexerProvider {
    fun provide(reader: Iterator<String>): Lexer
}
