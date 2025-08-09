package org.example.common.tokens.detectors

import org.example.common.tokens.Punctuation
import org.example.common.tokens.PunctuationSymbolsToken
import org.example.common.tokens.Token
import java.util.*

class PunctuationTokenDetector: TokenDetector {
    override fun detect(string: String): Optional<Token> {
        return try {
            val punctuation = Punctuation.fromString(string)
            Optional.of(PunctuationSymbolsToken(punctuation))
        } catch (e: NoSuchElementException) {
            Optional.empty()
        }    }
}