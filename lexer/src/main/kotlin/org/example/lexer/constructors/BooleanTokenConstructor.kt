package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.token.Token
import java.util.Optional

class BooleanTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.startsWith("true")) {
            return booleanToken(position, offset, "true")
        }
        if (input.startsWith("false")) {
            return booleanToken(position, offset, "false")
        }
        return Optional.empty()
    }

    private fun booleanToken(
        position: Position,
        offset: Int,
        value: String
    ): Optional<Token> {
        val tokenPosition = Position(position.line, offset)
        return Optional.of(Token(TokenType.BOOLEAN, value, tokenPosition))
    }
}
