package org.example.lexer.constructors

import org.example.common.Position
import org.example.token.Token
import org.example.token.TokenType
import java.util.*

class SymbolTokenConstructor : TokenConstructor {

    override fun constructToken(input: String, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstChar = input[0]
        if (!firstChar.isLetter() && firstChar != '_') return Optional.empty()

        val identifier = input.takeWhile { it.isLetterOrDigit() || it == '_' }

        return Optional.of(Token(TokenType.SYMBOL, identifier, position))
    }
}
