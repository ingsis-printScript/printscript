package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.enums.Types
import org.example.common.tokens.TypesToken
import org.example.common.tokens.Token
import java.util.*

class TypesTokenDetector: TokenDetector {
    override fun detect(string: String): Optional<Token> {
        return try {
            val type = Types.fromString(string)
            Optional.of(TypesToken(type, Range(0, 0)))
        } catch (e: NoSuchElementException) {
            Optional.empty()
        }
    }
}