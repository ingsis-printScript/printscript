package org.example.common.tokens.tokenizer

import org.example.common.Range
import org.example.common.tokens.LiteralToken
import org.example.common.tokens.LiteralType
import org.example.common.tokens.Token

class LiteralTokenizer: Tokenizer {
    override fun tokenize(string: String, range: Range): Token {
        //no puedo crear el raw con los /n porque el ejemplo de la pagina funciona como quiere
        val raw: String = string
        return if (string.toIntOrNull() != null) {
            LiteralToken(LiteralType.NUMBER, raw, string.toInt(), range)
        } else {
            LiteralToken(LiteralType.STRING, raw, string, range)
        }
    }
}