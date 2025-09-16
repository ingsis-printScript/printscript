package org.example.common.enums

enum class SymbolFormat(val values: List<String>) {
    CAMEL_CASE(listOf("camel_case", "camelcase", "camel case")),
    SNAKE_CASE(listOf("snake_case", "snakecase", "snake case"));

    companion object {
        fun fromString(value: String): SymbolFormat? {
            val lower = value.lowercase()
            return SymbolFormat.entries.firstOrNull { format ->
                lower in format.values
            }
        }
    }
}
