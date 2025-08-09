package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.Token

interface Tokenizer {
    fun tokenize(emptyToken: Token, string: String, range: Range): Token
}
