package org.example.common.tokens.enums

enum class Operator(val symbol: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    companion object {
        fun fromString(operator: String) =
            Operator.entries.first { it.symbol == operator }
    }
}