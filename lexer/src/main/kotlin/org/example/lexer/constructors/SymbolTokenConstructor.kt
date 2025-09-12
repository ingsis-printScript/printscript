package org.example.lexer.constructors

import org.example.common.Position
import org.example.common.enums.TokenType
import org.example.token.Token
import java.util.*

class SymbolTokenConstructor : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstChar = input[0]
        if (!firstChar.isLetter() && firstChar != '_') return Optional.empty()

        val identifier = input.takeWhile { it.isLetterOrDigit() || it == '_' }
        val tokenPosition = Position(position.line, offset + identifier.length)

        return Optional.of(Token(TokenType.SYMBOL, identifier, tokenPosition))
    }
}
