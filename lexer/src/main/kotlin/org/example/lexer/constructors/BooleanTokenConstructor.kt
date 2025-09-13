package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.token.Token
import java.util.Optional

class BooleanTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.startsWith("true")) {
            val tokenPosition = Position(position.line, offset)
            return Optional.of(Token(TokenType.BOOLEAN, "true", tokenPosition))
        }
        if (input.startsWith("false")) {
            val tokenPosition = Position(position.line, offset)
            return Optional.of(Token(TokenType.BOOLEAN, "false", tokenPosition))
        }
        return Optional.empty()
    }
}
