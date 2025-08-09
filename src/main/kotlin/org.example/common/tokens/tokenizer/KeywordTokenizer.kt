package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.KeywordToken
import org.example.common.tokens.Keywords
import org.example.common.tokens.Token

class KeywordTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyKeywordToken = emptyToken as KeywordToken
        return KeywordToken(emptyKeywordToken.kind, string, range)
    }
}