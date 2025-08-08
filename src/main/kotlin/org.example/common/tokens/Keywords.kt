package org.example.common.tokens

enum class Keywords() {
    VAR, LET, CONST, CONSOLE, LOG;

    companion object {
        fun fromString(word: String): Keywords =
            Keywords.entries.first { it.name.equals(word, ignoreCase = true) }
    }
}

// Por el momento solo hay LET.
// DUDA: ¿incluir 'string' y 'number' como keywords?
// ¿Si no, aislar LET a "DeclaratorToken"? Problema con límite de extensibilidad?
// Si es así, ¿qué pasa con Keywords...?