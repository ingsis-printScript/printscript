package org.example.lexer.provider

import org.example.lexer.Lexer

interface Provider {
    fun provide(reader: Iterator<String>): Lexer
}