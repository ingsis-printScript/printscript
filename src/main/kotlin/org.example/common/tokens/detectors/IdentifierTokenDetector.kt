package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import java.util.*

class IdentifierTokenDetector : TokenDetector {
    override fun detect(string: String): Optional<Token> {
        if (isValidIdentifier(string)) {
            return Optional.of(IdentifierToken("IDENTIFIER", "", Range(0, 0)))
        }
        return Optional.empty()
    }

    private fun isValidIdentifier(string: String): Boolean {
        if (string.isEmpty()) return false

        if (!string[0].isLetter() && string[0] != '_') {
            return false
        }

        for (i in 1 until string.length) {
            val char = string[i]
            if (!char.isLetterOrDigit() && char != '_') {
                return false
            }
        }

        return true
    }
}
