package org.example.common.tokens.enums

enum class Types {
    STRING, NUMBER;

    companion object {
        fun fromString(word: String): Types =  // <- Cambiar Keywords por Types
            Types.entries.first { it.name.equals(word, ignoreCase = true) }  // <- Cambiar Keywords por Types
    }
}