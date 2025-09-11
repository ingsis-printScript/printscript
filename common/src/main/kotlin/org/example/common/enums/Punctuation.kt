package org.example.common.enums

enum class Punctuation(val symbol: String) {
    EQUALS("="),
    SEMICOLON(";"),
    COLON(":"),
    COMMA(","),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]");

    companion object {
        fun fromString(symbol: String): Punctuation? {
            return Punctuation.entries.find { it.symbol == symbol }
        }
    }
}
