package org.example.lexer

import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.lexer.exceptions.NoMoreTokensAvailableException

class TokenBuffer(private val tokens: List<Token>) {
    private var currentIndex = 0

    fun peek(): Token {
        if (currentIndex >= tokens.size) { throw NoMoreTokensAvailableException("No more tokens available") }
        return tokens[currentIndex]
    }

    fun consume(expectedType: TokenType): Token {
        val token = peek()
        if (token.type != expectedType) {
            throw RuntimeException("Expected token of type $expectedType but found ${token.type}")
        }
        currentIndex++
        return token
    }

    fun isNext(expectedType: TokenType): Boolean {
        return currentIndex < tokens.size && tokens[currentIndex].type == expectedType
    }

    fun advance() {
        if (currentIndex >= tokens.size) {
            throw NoMoreTokensAvailableException("No more tokens available")
        }
        currentIndex++
    }

    fun isAtEnd(): Boolean {
        return currentIndex >= tokens.size
    }
}
