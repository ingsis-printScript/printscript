package org.example.common.tokens.detectors

import org.example.common.Position
import org.example.common.tokens.Token
import org.example.common.enums.TokenType
import java.util.*

class SymbolTokenConstructor : TokenConstructor {

    override fun constructToken(input: String, offset: Int, position: Position): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstChar = input[0]
        if (!firstChar.isLetter() && firstChar != '_') return Optional.empty()
        // No me encanta interferir con análisis sintáctico en el Lexer
        // O sea, siento que el análisis (sintáctico y semántico)
        // quedo desperdigado entre L y P y además se duplica

        val identifier = input.takeWhile { it.isLetterOrDigit() || it == '_' }
        val tokenPosition = Position(offset, offset + identifier.length)

        return Optional.of(Token(TokenType.SYMBOL, identifier, tokenPosition))
    }
}
