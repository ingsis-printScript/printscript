package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.PunctuationSymbolsToken
import org.example.common.tokens.Token

class PunctuationTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyPunctuationToken = emptyToken as PunctuationSymbolsToken
        return PunctuationSymbolsToken(emptyPunctuationToken.type, range)
    }
}