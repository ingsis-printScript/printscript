package org.example.lexer

import org.example.common.tokens.Token
import org.example.common.tokens.detectors.TokenDetector

class Lexer(
    private val detectors: List<TokenDetector>
) {

    fun lex(input: String): List<Token> {
        // función split -> genera inputs
        // función detect el tipo -> tokenDetector (devuelve el tokenizer a usar)
        // función tokenizer -> tokenizer (algo como una factory) -> devuelve el token acorde al input
        // poner el token en la lista
        return emptyList()
    }

    // nivel de dificultad = BAJO
    fun split(input: String): List<String> {
        return listOf(input)
    }

    // EN split:
    // ¿incluir separación de comas?
    // ¿incluir separación de posibles iguales sin espacios...?

    // nivel de dificultad = ALTO
    fun detectToken(input: String): String {
        TODO("Implementar lógica de detección de tipo de token")
    }

    // nivel de dificultad = MEDIO
    fun tokenizer(input: String): Token {
        TODO("Implementar lógica de creación del token")
    }
}
