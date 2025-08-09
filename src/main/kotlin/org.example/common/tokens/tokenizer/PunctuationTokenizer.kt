package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.PunctuationSymbolsToken
import org.example.common.tokens.Punctuation
import org.example.common.tokens.Token

class PunctuationTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyPunctuationToken = emptyToken as PunctuationSymbolsToken
        return PunctuationSymbolsToken(emptyPunctuationToken.type, range)
    }
}