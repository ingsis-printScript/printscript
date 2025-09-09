package org.example.common.enums

enum class TokenType {
    OPERATOR, PUNCTUATION, NUMBER, STRING, SYMBOL, BOOLEAN, KEYWORD;
//en punctuation esta el "=" mmm

    companion object {
        fun isElement(type: TokenType): Boolean {
            return type == SYMBOL || type == NUMBER || type == STRING
        }
    }
}