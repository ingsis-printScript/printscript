package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.PunctuationSymbolsToken
import org.example.common.tokens.Punctuation
import org.example.common.tokens.Token

class PunctuationTokenizer: Tokenizer {
    override fun tokenize(string: String, range: Range): Token {
        val type: Punctuation = Punctuation.fromString(string)
        return PunctuationSymbolsToken(type)
    }
}