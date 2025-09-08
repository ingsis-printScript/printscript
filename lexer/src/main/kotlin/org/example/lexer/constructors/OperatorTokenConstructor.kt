package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.interfaces.Operator
import java.util.*

class OperatorTokenConstructor(private val operators: Set<Operator>) : TokenConstructor {


    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val op = operators.find { it.value.equals(input, ignoreCase = true) }
        if (op != null) {
            val tokenPosition = Position(offset, offset + op.value.length)
            return Optional.of(Token(TokenType.OPERATOR, op.value, tokenPosition))
        }

        return Optional.empty()
    }
}
