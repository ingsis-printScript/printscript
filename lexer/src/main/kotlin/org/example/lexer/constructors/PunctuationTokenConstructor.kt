package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.token.TokenType
import java.util.*

class PunctuationTokenConstructor(private val punctuations: Set<String>) : TokenConstructor {

    override fun constructToken(input: String, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val punc = longestMatch(input)
        if (punc != null) {
            return Optional.of(Token(TokenType.PUNCTUATION, punc, position))
        }

        return Optional.empty()
    }

    private fun longestMatch(input: String) = punctuations.find { input.startsWith(it) }
}
