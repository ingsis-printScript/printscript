package org.example.lexer

import org.example.common.Position
import org.example.common.PrintScriptIterator
import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.common.exceptions.UnsupportedCharacterException
import org.example.lexer.constructors.KeywordTokenConstructor
import org.example.lexer.constructors.TokenConstructor
import org.example.token.Token
import java.util.*

class Lexer(
    private val reader: Iterator<String>,
    private val constructors: Collection<TokenConstructor>,
    private val keywords: KeywordTokenConstructor,
    private val whiteSpaces: List<Char>
) : PrintScriptIterator<Token> {

    private var currentLine: String = ""
    private var tokenOffset: Int = 0
    private var line: Int = 0

    override fun hasNext(): Boolean {
        if (noMoreLines()) { return false }

        if (ontoNextLine()) {
            currentLine = reader.next()
            tokenOffset = skipWhiteSpace(0)
            line++
        }

        while (onEmptyLine()) {
            currentLine = reader.next()
            tokenOffset = skipWhiteSpace(0)
            line++
        }

        val nextValidPosition = skipWhiteSpace(tokenOffset)
        return stillOnLine(nextValidPosition)
    }

    private fun onEmptyLine() = reader.hasNext() && currentLine.isEmpty()

    private fun noMoreLines() = (endedCurrentLine()) && !reader.hasNext()

    private fun endedCurrentLine() = currentLine.isEmpty() || ontoNextLine()

    override fun getNext(): Token {
        if (!hasNext()) throw NoMoreTokensAvailableException()

        tokenOffset = skipWhiteSpace(tokenOffset)
        if (ontoNextLine()) throw NoMoreTokensAvailableException()

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

    private fun ontoNextLine() = tokenOffset >= currentLine.length

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
        while (stillOnLine(offset) && isWhitespace(offset)) {
            offset++
        }
        return offset
    }

    private fun isWhitespace(offset: Int) = whiteSpaces.contains(currentLine[offset])

    private fun stillOnLine(offset: Int) = offset < currentLine.length
}
