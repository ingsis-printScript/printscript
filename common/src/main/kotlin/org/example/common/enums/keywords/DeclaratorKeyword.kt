package org.example.common.enums.keywords

import org.example.common.tokens.Keyword

enum class DeclaratorKeyword(override val value: String) : Keyword {
    LET("let"),
    CONST("const");

    companion object {
        fun isDeclaratorKeyword(value: String): Boolean {
            return entries.any { it.value.equals(value, ignoreCase = true) }
        }
    }
}
