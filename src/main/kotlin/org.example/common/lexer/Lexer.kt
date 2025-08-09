package org.example.common.lexer

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.detectors.TokenDetector
import org.example.common.tokens.tokenizers.Tokenizer

class Lexer(
    private val detectors: List<TokenDetector>,
    private val tokenizers: List<Tokenizer>
) {

    fun lex(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val words = split(input)

        for (word in words) {
            if(word.isNotBlank()){
                val range = calculateRange(input, word)

                // 1. DETECTAR - devuelve token vacío + índice del detector que funcionó
                val (emptyToken, detectorIndex) = detectToken(word)

                // 2. TOKENIZAR - usa el token vacío y el índice para completarlo
                val completeToken = tokenize(emptyToken, detectorIndex, word, range)

                tokens.add(completeToken)
            }
        }
        return tokens
    }

    // EN split:
    // ¿incluir separación de comas?
    // ¿incluir separación de posibles iguales sin espacios...?
    private fun split(input: String): List<String> {
        return input.split(" ").filter { it.isNotBlank() }
    }


     //FASE 1: DETECCIÓN
     //Identifica qué tipo de token es y devuelve el token vacío + el índice del detector que funcionó
     private fun detectToken(input: String): Pair<Token, Int> {
        for ((index, detector) in detectors.withIndex()) {
            val optionalToken = detector.detect(input)
            if (optionalToken.isPresent) {
                return Pair(optionalToken.get(), index)
            }
        }
        throw IllegalArgumentException("No token found for: '$input'")
    }

    //FASE 2: TOKENIZACIÓN
     //Usa el token vacío de la detección y lo completa con toda la información necesaria
     //El tokenizer RECIBE la información del detector para completar el token
    private fun tokenize(emptyToken: Token, detectorIndex: Int, input: String, range: Range): Token {
        return tokenizers[detectorIndex].tokenize(emptyToken, input, range)
    }

    private fun calculateRange(input: String, word: String): Range {
        val start = input.indexOf(word)
        return Range(start, start + word.length)
    }
}