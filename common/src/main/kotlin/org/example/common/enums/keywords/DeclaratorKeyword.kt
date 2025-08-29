package org.example.common.enums.keywords

enum class DeclaratorKeyword(override val value: String): Keyword {
    LET("let"),
    VAR("var"),
    CONST("const");

    companion object {
        fun isDeclaratorKeyword(value: String): Boolean {
            return entries.any { it.value.equals(value, ignoreCase = true) }
        }
    }
}