
package org.example.lexer

import org.example.common.tokens.Token
import org.example.lexer.exceptions.NoMoreTokensAvailableException

class TokenCollector(private val lexer: PrintScriptIterator<Token>) {

    // Convierte el iterador del Lexer en una lista de tokens

    fun getAllTokens(): List<Token> {
        val tokens = mutableListOf<Token>()

        try {
            while (lexer.hasNext()) {
                tokens.add(lexer.getNext())
            }
        } catch (e: NoMoreTokensAvailableException) {
        }

        return tokens
    }
}
