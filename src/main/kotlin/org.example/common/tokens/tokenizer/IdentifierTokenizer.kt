package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.IdentifierToken
import org.example.common.tokens.Token

class IdentifierTokenizer: Tokenizer {
    override fun tokenize(string: String, range: Range): Token {
        val token: IdentifierToken = IdentifierToken("Identify", string, range)
        return token
    }
}