package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.PunctuationToken
import org.example.common.tokens.enums.Punctuation
import org.example.common.tokens.Token

class PunctuationTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyPunctuationToken = emptyToken as PunctuationToken
        return PunctuationToken(emptyPunctuationToken.type, range)
    }
}