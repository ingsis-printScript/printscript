package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.token.Token
import java.util.*

class OperatorTokenConstructor(private val operators: Set<String>) : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val op = longestMatch(input)
        if (op != null) {
            val tokenPosition = Position(position.line, offset)
            return Optional.of(Token(TokenType.OPERATOR, op, tokenPosition))
        }

        return Optional.empty()
    }

    private fun longestMatch(input: String) = operators.find { input.startsWith(it) }
}
