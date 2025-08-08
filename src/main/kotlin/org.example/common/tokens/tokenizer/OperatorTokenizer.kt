package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.Operator
import org.example.common.tokens.OperatorToken
import org.example.common.tokens.Token

class OperatorTokenizer: Tokenizer {
    override fun tokenize(string: String, range: Range): Token {
        val type: Operator = Operator.fromString(string)
        return OperatorToken(type)
    }
}