package org.example.common.tokens.detectors

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class OperatorTokenConstructor : TokenConstructor {

    private val operators = setOf(
        "+", "-", "*", "/", "%", "!=", "<", "<=", ">", ">=", "&&", "||", "!"
    )

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        for (op in operators) {
            if (input.startsWith(op)) {
                val tokenPosition = Position(offset, offset + op.length)
                return Optional.of(Token(TokenType.OPERATOR, op, tokenPosition))
            }
        }

        return Optional.empty()
    }
}
