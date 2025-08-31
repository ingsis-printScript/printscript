package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import java.util.Optional

class NumberTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty() || !input[0].isDigit()) return Optional.empty()

        val numberStr = input.takeWhile { it.isDigit() }

        val tokenPosition = Position(offset, offset + numberStr.length)
        return Optional.of(Token(TokenType.NUMBER, numberStr, tokenPosition))
    }
}
