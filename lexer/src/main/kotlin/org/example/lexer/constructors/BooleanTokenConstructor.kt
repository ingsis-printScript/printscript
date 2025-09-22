package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.TokenType
import org.example.token.Token
import java.util.Optional

class BooleanTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, position: Position): Optional<Token> {
        if (input.startsWith("true")) {
            return booleanToken(position, "true")
        }
        if (input.startsWith("false")) {
            return booleanToken(position, "false")
        }
        return Optional.empty()
    }

    private fun booleanToken(
        position: Position,
        value: String
    ): Optional<Token> {
        return Optional.of(Token(TokenType.BOOLEAN, value, position))
    }
}
