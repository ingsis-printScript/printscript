package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.common.tokens.Punctuation
import org.example.token.Token
import java.util.*

class PunctuationTokenConstructor(private val punctuations: Set<Punctuation>) : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val punc = longestMatch(input)
        if (punc != null) {
            val tokenPosition = Position(position.line, offset + punc.value.length)
            return Optional.of(Token(TokenType.PUNCTUATION, punc.value, tokenPosition))
        }

        return Optional.empty()
    }

    private fun longestMatch(input: String) = punctuations.find { input.startsWith(it.value) }
}
