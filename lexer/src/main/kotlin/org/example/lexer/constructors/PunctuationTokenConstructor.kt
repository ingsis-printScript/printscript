package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.token.Token
import java.util.*

class PunctuationTokenConstructor(private val punctuations: Set<String>) : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val punc = longestMatch(input)
        if (punc != null) {
            val tokenPosition = Position(position.line, offset + punc.length)
            return Optional.of(Token(TokenType.PUNCTUATION, punc, tokenPosition))
        }

        return Optional.empty()
    }

    private fun longestMatch(input: String) = punctuations.find { input.startsWith(it) }
}
