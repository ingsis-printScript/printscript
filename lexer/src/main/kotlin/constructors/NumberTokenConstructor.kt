package org.example.common.tokens.constructors

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.common.tokens.detectors.TokenConstructor
import java.util.Optional

class NumberTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty() || !input[0].isDigit()) return Optional.empty()

        val numberStr = input.takeWhile { it.isDigit() }

        val tokenPosition = Position(offset, offset + numberStr.length)
        return Optional.of(Token(TokenType.NUMBER, numberStr, tokenPosition))
    }
}
