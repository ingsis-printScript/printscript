package org.example.common.enums.keywords

enum class ConditionalKeyword(override val value: String) : Keyword {
    IF("if"),
    ELSE("else"),
    WHEN("when");


    companion object {
        fun isDeclaratorKeyword(value: String): Boolean {
            return entries.any { it.value.equals(value, ignoreCase = true) }
        }
    }
}