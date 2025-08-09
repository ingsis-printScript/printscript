package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Operator
import org.example.common.tokens.OperatorToken
import org.example.common.tokens.Token
import java.util.*

class OperatorTokenDetector: TokenDetector {
    override fun detect(string: String): Optional<Token> {
        return try {
            val operator = Operator.fromString(string)
            Optional.of(OperatorToken(operator))
        } catch (e: NoSuchElementException) {
            Optional.empty()
        }
    }
}