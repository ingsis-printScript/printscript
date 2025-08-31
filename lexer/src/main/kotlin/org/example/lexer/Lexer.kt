package org.example.lexer

import org.example.common.Position
import org.example.token.Token
import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.TokenConstructor
import org.example.lexer.exceptions.NoMoreTokensAvailableException
import org.example.lexer.exceptions.UnsupportedCharacterException
import java.util.*

class Lexer(
    private val reader: Iterator<String>, // el reader lee lines? chars? o el input completo?
    private val constructors: Collection<TokenConstructor>,
    private val keywords: KeywordTokenConstructor,
    private val whiteSpaces: List<Char>
) : PrintScriptIterator<Token> {

    private var currentLine: String = ""
    private var tokenOffset: Int = 0
    private var line: Int = 0 // como currLine es "", line iniciada en 1 trae problemas

    override fun hasNext(): Boolean {
        if ((endedCurrentLine()) && !reader.hasNext()) {
            return false
        }

        if (tokenOffset >= currentLine.length) {
            currentLine = reader.next()
            tokenOffset = skipWhiteSpace(0)
            line++
        } // quizás llamaría la condition "ontoNextLine" o algo así?
        // no me encanta ese nombre porque se confunde con el loop de abajo igual...

        while (reader.hasNext() && currentLine.isEmpty()) {
            currentLine = reader.next()
            tokenOffset = skipWhiteSpace(0) // hice que sWS devuelva para claridad
            line++
        }

        val nextValidPosition = skipWhiteSpace(tokenOffset)
        return nextValidPosition < currentLine.length
    }

    private fun endedCurrentLine() = currentLine.isEmpty() || tokenOffset >= currentLine.length

    override fun getNext(): Token {
        if (!hasNext()) throw NoMoreTokensAvailableException()

        tokenOffset = skipWhiteSpace(tokenOffset)
        if (tokenOffset >= currentLine.length) throw NoMoreTokensAvailableException()

        val currentCharacter = currentLine[tokenOffset]
        val optionalToken = getOptionalToken()

        if (optionalToken.isPresent) {
            val token = optionalToken.get()
            tokenOffset += token.value.length
            return token
        }

        throw UnsupportedCharacterException(
            "Unsupported character '$currentCharacter' at line $line, column $tokenOffset"
        )
    }

    private fun getOptionalToken(): Optional<Token> {
        val s = currentLine.substring(tokenOffset)
        val startPosition = Position(line, tokenOffset)

        return keywords.constructToken(s, tokenOffset, startPosition)
            .or {
                Optional.ofNullable(
                    constructors.asSequence()
                        .map { it.constructToken(s, tokenOffset, startPosition) }
                        .filter { it.isPresent }
                        .map { it.get() }
                        .maxByOrNull { it.value.length }
                )
            }
    }

    private fun skipWhiteSpace(tokenOffset: Int): Int {
        var offset: Int = tokenOffset
        while (offset < currentLine.length && whiteSpaces.contains(currentLine[offset])) {
            offset++
        }
        return offset
    }
}
