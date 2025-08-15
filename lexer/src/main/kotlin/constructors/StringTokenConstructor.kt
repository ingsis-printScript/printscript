package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class StringTokenConstructor: TokenConstructor {

    override fun constructToken(input: String, offset: Int, range: Range): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val quoteChar = input[0]
        if (quoteChar != '"' && quoteChar != '\'') return Optional.empty()

        val closingIndex = input.indexOf(quoteChar, startIndex = 1)
        if (closingIndex == -1) return Optional.empty()

        val strValue = input.substring(0, closingIndex + 1)
        val tokenRange = Range(offset, offset + strValue.length)
        return Optional.of(Token(TokenType.STRING, strValue, tokenRange))
    }
}
