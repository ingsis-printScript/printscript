package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.LiteralToken
import org.example.common.tokens.enums.LiteralType
import org.example.common.tokens.Token
import java.util.*

class LiteralTypeTokenDetector: TokenDetector {
    override fun detect(string: String): Optional<Token> {

        if (isStringLiteral(string)) {
            return Optional.of(LiteralToken(LiteralType.STRING, string, "", Range(0, 0)))
        }

        if (isNumberLiteral(string)) {
            return Optional.of(LiteralToken(LiteralType.NUMBER, string, 0, Range(0, 0)))
        }

        return Optional.empty()
    }

    private fun isStringLiteral(string: String): Boolean {
        return (string.length >= 2) &&
                ((string.startsWith("\"") && string.endsWith("\"")) ||
                        (string.startsWith("'") && string.endsWith("'")))
    }

    private fun isNumberLiteral(string: String): Boolean {
        return try {
            string.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}