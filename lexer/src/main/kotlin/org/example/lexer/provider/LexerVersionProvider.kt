package org.example.lexer.provider

class LexerVersionProvider {
    fun with(version: String): LexerProvider {
        return when (version) {
            "1.0" -> LexerProvider10()
            "1.1" -> LexerProvider11()
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}
