package org.example.common.enums

enum class Operator(val symbol: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%");

    companion object {
        fun fromString(symbol: String): Operator? {
            return Operator.entries.find { it.symbol == symbol }
        }
    }
}
