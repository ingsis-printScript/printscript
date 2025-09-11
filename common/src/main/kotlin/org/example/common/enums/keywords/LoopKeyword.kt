package org.example.common.enums.keywords

import org.example.common.tokens.Keyword

enum class LoopKeyword(override val value: String) : Keyword {
    FOR("for"),
    WHILE("while"),
    DO("do");

    companion object {
        fun isDeclaratorKeyword(value: String): Boolean {
            return DeclaratorKeyword.entries.any { it.value.equals(value, ignoreCase = true) }
        }
    }
}
