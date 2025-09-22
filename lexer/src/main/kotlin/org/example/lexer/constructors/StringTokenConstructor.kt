package org.example.lexer.constructors
import org.example.common.Position
import org.example.token.TokenType
import org.example.token.Token
import java.util.*

class StringTokenConstructor : TokenConstructor {

    override fun constructToken(input: String, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val quoteChar = input[0]
        if (quoteChar != '"' && quoteChar != '\'') return Optional.empty()

        val closingIndex = input.indexOf(quoteChar, startIndex = 1)
        if (closingIndex == -1) return Optional.empty()

        val strValue = input.substring(0, closingIndex + 1)
        return Optional.of(Token(TokenType.STRING, strValue, position))
    }
}
