package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class PunctuationTokenConstructor: TokenConstructor {

    private val punctuations = setOf(
        "=", ";", ":", ",", "(", ")", "{", "}", "[", "]"
    )

    override fun constructToken(input: String, offset: Int, range: Range): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstChar = input[0].toString()
        if (punctuations.contains(firstChar)) {
            val tokenRange = Range(offset, offset + 1)
            return Optional.of(Token(TokenType.PUNCTUATION, firstChar, tokenRange))
        }

        return Optional.empty()
    }
}