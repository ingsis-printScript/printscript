package org.example.common.tokens.tokenizers

import org.example.common.Range
import org.example.common.tokens.LiteralToken
import org.example.common.tokens.enums.LiteralType
import org.example.common.tokens.Token

class LiteralTokenizer: Tokenizer {
    override fun tokenize(emptyToken: Token, string: String, range: Range): Token {
        val emptyLiteralToken = emptyToken as LiteralToken<*>
        val raw = string

        return when (emptyLiteralToken.type) {
            LiteralType.NUMBER -> {
                val value = string.toDouble()
                LiteralToken(LiteralType.NUMBER, raw, value, range)
            }

            LiteralType.STRING -> {
                // Sacar comillas - ya sabemos que es string válido
                val value = if ((string.startsWith("\"") && string.endsWith("\"")) ||
                    (string.startsWith("'") && string.endsWith("'"))) {
                    string.substring(1, string.length - 1)
                } else {
                    string
                }
                LiteralToken(LiteralType.STRING, raw, value, range)
            }
        }
    }
}