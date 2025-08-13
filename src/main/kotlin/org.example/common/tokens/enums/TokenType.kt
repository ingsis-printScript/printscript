package org.example.common.tokens.enums

enum class TokenType {
    KEYWORD, IDENTIFIER, LITERAL, OPERATOR, PUNCTUATION, TYPE
}


companion object {
    fun fromString(type: String): TokenType? {
        return values().find { it.name.equals(type, ignoreCase = true) }
}