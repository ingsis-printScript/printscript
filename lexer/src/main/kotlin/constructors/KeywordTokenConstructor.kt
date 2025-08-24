package org.example.common.tokens.detectors

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class KeywordTokenConstructor : TokenConstructor {

    private val keywords = setOf(
        "if", "else", "while", "for", "function", "return", "let", "var", "const",
        "true", "false", "null", "print", "println", "number", "string", "boolean"
    )

    // DUDA: NUMBER y STRING no son literales? o sea, son 2, "hola"... no?
    // o sea, si recibo "number", eso es un keyword, pero con este map convierte a token de tipo literal
    private val literals = mapOf("number" to TokenType.NUMBER, "string" to TokenType.STRING)

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstWord = input.takeWhile { it.isLetter() } // toma la primera "palabra"
        if (firstWord.isEmpty()) return Optional.empty()

        val tokenType = when {
            keywords.contains(firstWord) -> TokenType.KEYWORD
            literals.containsKey(firstWord) -> literals[firstWord]!!
            else -> return Optional.empty()
        }

        return Optional.of(Token(tokenType, firstWord, position))
    }
}
