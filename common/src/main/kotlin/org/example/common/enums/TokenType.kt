package org.example.common.enums

enum class TokenType {
    OPERATOR, PUNCTUATION, NUMBER, STRING, SYMBOL, KEYWORD;

    companion object {
        fun isElement(type: TokenType): Boolean {
            return type == SYMBOL || type == NUMBER || type == STRING
        }
    }
}