package org.example.parser.parsers.builders.expression.rules

import org.example.token.TokenType
import org.example.parser.exceptions.SyntaxException
import org.example.token.Token

class ParenthesesHandler {
    fun findMatchingCloseParen(tokens: List<Token>, openParenIndex: Int): Int {
        var depth = 1
        for (i in (openParenIndex + 1) until tokens.size) {
            when {
                isOpenParen(tokens[i]) -> depth++
                isCloseParen(tokens[i]) -> {
                    depth--
                    if (depth == 0) return i
                }
            }
        }
        throw SyntaxException("Unmatched opening parenthesis at position ${tokens[openParenIndex].position}")
    }

    fun isOpenParen(token: Token): Boolean {
        return token.type == TokenType.PUNCTUATION && token.value == "("
    }

    fun isCloseParen(token: Token): Boolean {
        return token.type == TokenType.PUNCTUATION && token.value == ")"
    }
}
