package org.example.common.tokens

enum class TokenType {
    OPERATOR, PUNCTUATION, NUMBER, STRING, SYMBOL, KEYWORD;

    companion object {
        fun fromString(type: String): TokenType? {
            return TokenType.entries.find { it.name.equals(type, ignoreCase = true) }
        }
        fun isElement(type: TokenType): Boolean {
            return type == SYMBOL || type == NUMBER || type == STRING
        }
    }
}
