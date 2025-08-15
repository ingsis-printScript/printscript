package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*


class KeywordTokenConstructor: TokenConstructor {

    private val keywords = setOf(
        "if", "else", "while", "for", "function", "return", "var", "const",
        "true", "false", "null", "print", "println", "number", "string", "boolean"
    )

    private val literals = mapOf("number" to TokenType.NUMBER, "string" to TokenType.STRING)

    override fun constructToken(input: String, offset: Int, range: Range): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstWord = input.takeWhile { it.isLetter() } // toma la primera "palabra"
        if (firstWord.isEmpty()) return Optional.empty()

        val tokenType = when {
            literals.containsKey(firstWord) -> literals[firstWord]!!
            keywords.contains(firstWord) -> TokenType.KEYWORD
            else -> return Optional.empty()
        }

        return Optional.of(Token(tokenType, firstWord, range))
    }

}