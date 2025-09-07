package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.enums.Operator
import java.util.*

class OperatorTokenConstructor : TokenConstructor {

    val operators = Operator.entries.map { it.symbol }

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
