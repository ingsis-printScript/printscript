package org.example.common.tokens

enum class Punctuation(val symbol: String) {
    COMMA(","),
    SEMICOLON(";"),
    COLON(":"),
    DOT("."),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]");

    companion object {
        fun fromString(symbol: String): Punctuation =
            Punctuation.entries.first { it.symbol == symbol }
    }
}