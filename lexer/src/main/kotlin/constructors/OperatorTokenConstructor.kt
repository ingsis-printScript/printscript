package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class OperatorTokenConstructor: TokenConstructor {

    private val operators = setOf(
        "+", "-", "*", "/", "%", "==", "!=", "<", "<=", ">", ">=", "&&", "||", "!"
    )

    override fun constructToken(input: String, offset: Int, range: Range): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        for (op in operators) {
            if (input.startsWith(op)) {
                val tokenRange = Range(offset, offset + op.length)
                return Optional.of(Token(TokenType.OPERATOR, op, tokenRange))
            }
        }

        return Optional.empty()
    }
}