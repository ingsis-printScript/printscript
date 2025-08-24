package org.example.common.tokens.detectors

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class PunctuationTokenConstructor : TokenConstructor {

    private val punctuations = setOf(
        "=", ";", ":", ",", "(", ")", "{", "}", "[", "]"
    )

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        // que pasa si recibo ";;;;;" o ",hola"
        val firstChar = input[0].toString()
        if (punctuations.contains(firstChar)) {
            val tokenPosition = Position(offset, offset + 1)
            return Optional.of(Token(TokenType.PUNCTUATION, firstChar, tokenPosition))
        }

        return Optional.empty()
    }
}
