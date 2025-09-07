package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.enums.Punctuation
import java.util.*

class PunctuationTokenConstructor : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstChar = input[0].toString()
        if (Punctuation.fromString(firstChar) != null) {
            val tokenPosition = Position(offset, offset + 1)
            return Optional.of(Token(TokenType.PUNCTUATION, firstChar, tokenPosition))
        }

        return Optional.empty()
    }
}
