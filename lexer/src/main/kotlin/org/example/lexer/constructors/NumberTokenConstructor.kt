package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.token.Token
import java.util.Optional

class NumberTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty() || !input[0].isDigit()) return Optional.empty()

        var hasDot = false
        val numberStr = buildString {
            for (ch in input) {
                if (ch.isDigit()) {
                    append(ch)
                } else if (ch == '.' && !hasDot) {
                    hasDot = true
                    append(ch)
                } else {
                    break
                }
            }
        }

        val tokenPosition = Position(position.line, offset)
        return Optional.of(Token(TokenType.NUMBER, numberStr, tokenPosition))
    }
}
