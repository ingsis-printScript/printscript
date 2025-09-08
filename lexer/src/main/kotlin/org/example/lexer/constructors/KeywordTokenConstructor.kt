package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.tokens.Keyword
import java.util.*

class KeywordTokenConstructor(private val keywords: Set<Keyword>) : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstWord = input.takeWhile { it.isLetter() } // toma la primera "palabra"
        if (firstWord.isEmpty()) return Optional.empty()

        val tokenType = when {
            keywords.any { it.value.equals(firstWord, ignoreCase = true) } -> TokenType.KEYWORD
            else -> return Optional.empty()
        }

        return Optional.of(Token(tokenType, firstWord, position))
    }
}
