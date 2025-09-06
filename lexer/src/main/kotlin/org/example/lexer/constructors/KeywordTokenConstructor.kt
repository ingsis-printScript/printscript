package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.common.enums.TokenType
import java.util.*

class KeywordTokenConstructor : TokenConstructor {

    private val keywords = setOf(
        "if", "else", "while", "for", "function", "return", "let", "var", "const",
        "true", "false", "null", "print", "println", "boolean"
    ) //TODO(cambiar a que reciba desde afuera)

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
