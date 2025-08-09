package org.example.common.tokens.detectors

import org.example.common.tokens.Token
import java.util.*

interface TokenDetector {
    fun detect(string: String): Optional<Token>

}