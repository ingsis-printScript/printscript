package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.enums.keywords.ConditionalKeyword
import org.example.common.enums.keywords.DeclaratorKeyword
import org.example.common.enums.keywords.LoopKeyword
import java.util.*

class KeywordTokenConstructor : TokenConstructor {

    private val keywords = DeclaratorKeyword.entries.map { it.value }.toSet() +
        LoopKeyword.entries.map { it.value } +
        ConditionalKeyword.entries.map { it.value }

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstWord = input.takeWhile { it.isLetter() } // toma la primera "palabra"
        if (firstWord.isEmpty()) return Optional.empty()

        val tokenType = when {
            keywords.contains(firstWord) -> TokenType.KEYWORD
            else -> return Optional.empty()
        }

        return Optional.of(Token(tokenType, firstWord, position))
    }
}
