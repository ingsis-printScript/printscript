package org.example.common.tokens.constructors

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import org.example.common.tokens.detectors.TokenConstructor
import java.util.Optional

class NumberTokenConstructor : TokenConstructor {
    override fun constructToken(input: String, offset: Int, range: Range): Optional<Token> {
        if (input.isEmpty() || !input[0].isDigit()) return Optional.empty()

        val numberStr = input.takeWhile { it.isDigit() }

        val tokenRange = Range(offset, offset + numberStr.length)
        return Optional.of(Token(TokenType.NUMBER, numberStr, tokenRange))
    }
}
