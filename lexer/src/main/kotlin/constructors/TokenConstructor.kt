package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import java.util.*

interface TokenConstructor {
    fun constructToken(input: String, offset: Int, range: Range): Optional<Token>
}