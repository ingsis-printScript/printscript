package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.Token

// Todo: Implement TypesTokenizer
interface Tokenizer {
    fun tokenize(emptyToken: Token, string: String, range: Range): Token
}
