package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.IdentifierToken
import org.example.common.tokens.Token

class IdentifierTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyIdentifierToken = emptyToken as IdentifierToken
        return IdentifierToken(emptyIdentifierToken.type, string, range)
    }
}