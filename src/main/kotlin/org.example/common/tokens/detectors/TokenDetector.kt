package org.example.common.tokens.detectors

import org.example.common.tokens.tokenizer.Tokenizer
import java.util.*

interface TokenDetector {
    fun detect(string: String): Optional<String>
    fun getTokenizer(string: String): Tokenizer

}