package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.OperatorToken
import org.example.common.tokens.Token

class OperatorTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyOperatorToken = emptyToken as OperatorToken
        return OperatorToken(emptyOperatorToken.type, range)
    }
}