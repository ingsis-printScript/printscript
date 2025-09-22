package org.example.lexer

import org.example.common.Position
import org.example.common.PrintScriptIterator
import org.example.token.TokenType
import org.example.common.exceptions.NoMoreTokensAvailableException
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
    private var currentColumn: Int = 1
    private var currentLineNumber: Int = 0

    override fun hasNext(): Boolean {
        if (noMoreLines()) { return false }

        if (ontoNextLine()) {
            currentLine = reader.next()
            currentColumn = skipWhiteSpaceAndGetColumn(1)
            currentLineNumber++
        }

        while (onEmptyLine()) {
            currentLine = reader.next()
            currentColumn = skipWhiteSpaceAndGetColumn(1)
            currentLineNumber++
        }

        val nextValidColumn = skipWhiteSpaceAndGetColumn(currentColumn)
        return stillOnLine(nextValidColumn)
    }

    private fun onEmptyLine() = reader.hasNext() && currentLine.isEmpty()

    private fun noMoreLines() = (endedCurrentLine()) && !reader.hasNext()

    private fun endedCurrentLine() = currentLine.isEmpty() || ontoNextLine()

    override fun getNext(): Token {
        if (!hasNext()) {
            throw NoMoreTokensAvailableException()
        }

        currentColumn = skipWhiteSpaceAndGetColumn(currentColumn)
        if (ontoNextLine()) {
            throw NoMoreTokensAvailableException()
        }

        val optionalToken = getOptionalToken()

        if (optionalToken.isPresent) {
            val token = optionalToken.get()
            currentColumn += token.value.length
            return token
        }

        val ch = currentLine[toArrayIndex(currentColumn)].toString()
        val tok = Token(TokenType.UNKNOWN, ch, Position(currentLineNumber, currentColumn))
        currentColumn += 1
        return tok
    }

    private fun ontoNextLine() = toArrayIndex(currentColumn) >= currentLine.length

    private fun getOptionalToken(): Optional<Token> {
        val arrayIndex = toArrayIndex(currentColumn)
        val s = currentLine.substring(arrayIndex)
        val startPosition = Position(currentLineNumber, currentColumn)

        return keywords.constructToken(s, startPosition)
            .or {
                Optional.ofNullable(
                    constructors.asSequence()
                        .map { it.constructToken(s, startPosition) }
                        .filter { it.isPresent }
                        .map { it.get() }
                        .maxByOrNull { it.value.length }
                )
            }
    }

    private fun skipWhiteSpaceAndGetColumn(startColumn: Int): Int {
        var column = startColumn
        while (stillOnLine(column) && isWhitespace(column)) {
            column++
        }
        return column
    }

    private fun isWhitespace(column: Int): Boolean {
        val arrayIndex = toArrayIndex(column)
        return whiteSpaces.contains(currentLine[arrayIndex])
    }

    private fun stillOnLine(column: Int) = toArrayIndex(column) < currentLine.length

    private fun toArrayIndex(column: Int) = column - 1
}
