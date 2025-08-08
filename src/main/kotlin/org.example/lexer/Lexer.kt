package org.example.lexer

import org.example.common.tokens.Token
import org.example.common.tokens.detectors.TokenDetector

class Lexer(
    private val detectors: List<TokenDetector>
) {

    fun lex(input: String): List<Token> {
        // función split -> genera inputs
        // función detect del TokenDetector -> tokenDetector (devuelve el tokenizer a usar)
        // funcion tokenizer del TokenDetector
        // función tokenizer -> tokenizer (algo como una factory) -> devuelve el token acorde al input
        // poner el token en la lista
        return emptyList()
    }

    // nivel de dificultad = BAJO
    fun split(input: String): List<String> {
        return input.split(" ").filter { it.isNotBlank() }
    }
    //let a: string="hello" string hello

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


// let
//que es let?
//llamo a detector para saber que es let?
//se fija dentro de sus detectors cual es, resultado es un Keyword
