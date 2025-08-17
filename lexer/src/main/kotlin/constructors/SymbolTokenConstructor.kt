package org.example.common.tokens.detectors

import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.TokenType
import java.util.*

class SymbolTokenConstructor : TokenConstructor {


    override fun constructToken(input: String, offset: Int, range: Range): Optional<Token> {
        if (input.isEmpty()) return Optional.empty()

        val firstChar = input[0]
        if (!firstChar.isLetter() && firstChar != '_') return Optional.empty()
        // No me encanta interferir con análisis sintáctico en el Lexer
        // O sea, siento que el análisis (sintáctico y semántico)
        // quedo desperdigado entre L y P y además se duplica

        val identifier = input.takeWhile { it.isLetterOrDigit() || it == '_' }
        val tokenRange = Range(offset, offset + identifier.length)

        return Optional.of(Token(TokenType.SYMBOL, identifier, tokenRange))
    }

}
