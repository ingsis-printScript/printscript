package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.enums.Punctuation
import org.example.common.tokens.PunctuationSymbolsToken
import org.example.common.tokens.Token
import java.util.*

class PunctuationTokenDetector: TokenDetector {
    override fun detect(string: String): Optional<Token> {
        return try {
            val punctuation = Punctuation.fromString(string)
            Optional.of(PunctuationSymbolsToken(punctuation, Range(0, 0)))
        } catch (e: NoSuchElementException) {
            Optional.empty()
        }    }
}