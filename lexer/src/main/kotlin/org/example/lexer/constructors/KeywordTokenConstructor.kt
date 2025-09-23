package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.token.TokenType
import java.util.*

class KeywordTokenConstructor(private val keywords: Set<String>) : TokenConstructor {

    override fun constructToken(input: String, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstWord = input.takeWhile { it.isLetter() } // toma la primera "palabra"
        if (firstWord.isEmpty()) return Optional.empty()

        val tokenType = when {
            keywords.any { it.equals(firstWord, ignoreCase = true) } -> TokenType.KEYWORD
            else -> return Optional.empty()
        }

        return Optional.of(Token(tokenType, firstWord, position))
    }
}
