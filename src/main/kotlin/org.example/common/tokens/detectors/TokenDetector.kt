package org.example.common.tokens.detectors

import org.example.common.tokens.tokenizer.Tokenizer

interface TokenDetector {
    fun detect(string: String): Tokenizer
}