package org.example.common.tokens

enum class TokenType {
    OPERATOR, PUNCTUATION, NUMBER, STRING, SYMBOL, KEYWORD;
    //TODO("LITERAL? -> necesito distinguir entre IDENTIFIER y LITERAL")


    companion object {
        fun fromString(type: String): TokenType? {
            return values().find { it.name.equals(type, ignoreCase = true) }
        }
        fun isElement(type: TokenType): Boolean {
            return type == SYMBOL || type == NUMBER || type == STRING
        }
    }
}