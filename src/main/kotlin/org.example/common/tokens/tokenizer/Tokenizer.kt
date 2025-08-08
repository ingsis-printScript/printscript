package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.Token

interface Tokenizer {
    fun tokenize(string: String, range: Range): Token
}
