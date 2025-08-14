package org.example.common.tokens.enums

enum class TokenType {
     OPERATOR, PUNCTUATION, NUMBER, STRING, SYMBOL, KEYWORD
}


companion object {
    fun fromString(type: String): TokenType? {
        return values().find { it.name.equals(type, ignoreCase = true) }
}