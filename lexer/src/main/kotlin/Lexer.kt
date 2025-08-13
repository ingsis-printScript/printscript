package org.example.lexer

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

    private fun split(input: String): List<String> { //en vez de todo junto, recibir hasta que .... (procesar por sentencia)
        val currentTerm = StringBuilder()

        val splitInput = processInput(input, currentTerm)

        finishCurrentTerm(currentTerm, splitInput)

        return splitInput.filter { it.isNotBlank() }
    }

    private fun processInput(
        input: String,
        currentTerm: StringBuilder
    ): MutableList<String> {
        val splitInput = mutableListOf<String>()
        var stringDelimiter: Char? = null
        var inString = false

        var i = 0
        while (i < input.length) {
            val char = input[i]

            when {
                startingString(char, inString) -> {
                    finishCurrentTerm(currentTerm, splitInput)
                    inString = true
                    stringDelimiter = char
                    currentTerm.append(char)
                }

                endingString(char, stringDelimiter, inString) -> {
                    currentTerm.append(char)
                    finishCurrentTerm(currentTerm, splitInput)
                    inString = false
                    stringDelimiter = null
                }

                inString -> { currentTerm.append(char) }

                char.isWhitespace() -> { finishCurrentTerm(currentTerm, splitInput) }

                char in "=:;" -> {
                    finishCurrentTerm(currentTerm, splitInput)
                    splitInput.add(char.toString())
                }

                else -> { currentTerm.append(char) }
            }
            i++
        }
        return splitInput
    }

    private fun endingString(char: Char, stringDelimiter: Char?, inString: Boolean) =
        char == stringDelimiter && inString

    private fun startingString(char: Char, inString: Boolean) = (char == '"' || char == '\'') && !inString

    private fun finishCurrentTerm(currentTerm: StringBuilder, result: MutableList<String>) {
        if (currentTerm.isNotEmpty()) {
            result.add(currentTerm.toString())
            currentTerm.clear()
        }
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