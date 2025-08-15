package org.example.lexer

import com.sun.org.apache.xpath.internal.compiler.Keywords
import org.example.common.Range
import org.example.common.tokens.Token
import org.example.common.tokens.detectors.KeywordTokenConstructor
import org.example.common.tokens.detectors.TokenConstructor
import org.example.lexer.exceptions.NoMoreTokensAvailableException
import org.example.lexer.exceptions.UnsupportedCharacterException
import java.util.*

class Lexer(
    private val reader: Iterator<String>,
    private val constructors: Collection<TokenConstructor>,
    private val keywords: KeywordTokenConstructor,
    private val whiteSpaces: List<Char>
) : PrintScriptIterator<Token> {

    private var currentLine: String = ""
    private var offset: Int = 0
    private var line: Int = 1

    override fun hasNext(): Boolean {
        if ((currentLine.isEmpty() || offset >= currentLine.length) && !reader.hasNext()) {
            return false
        }

        if (offset >= currentLine.length) {
            currentLine = reader.next()
            offset = 0
            skipWhiteSpace()
            line++
        }

        while (reader.hasNext() && currentLine.isEmpty()) {
            currentLine = reader.next()
            offset = 0
            skipWhiteSpace()
            line++
        }

        return currentLine.isNotEmpty()
    }

    override fun getNext(): Token {
        if (!hasNext()) throw NoMoreTokensAvailableException()

        skipWhiteSpace()
        if (offset >= currentLine.length) throw NoMoreTokensAvailableException()

        val currentCharacter = currentLine[offset]
        val optionalToken = getOptionalToken()

        if (optionalToken.isPresent) {
            val token = optionalToken.get()
            offset += token.value.length
            return token
        }

        throw UnsupportedCharacterException(
            "Unsupported character '$currentCharacter' at line $line, column $offset"
        )
    }

    private fun getOptionalToken(): Optional<Token> {
        val s = currentLine.substring(offset)
        val startRange = Range(line, offset)
        return keywords.constructToken(s, offset, startRange)
            .or {
                constructors.asSequence()
                    .map { it.constructToken(s, offset, startRange) }
                    .filter { it.isPresent }
                    .map { it.get() }
                    .maxByOrNull { it.value.length }
                    ?.let { Optional.of(it) }
            }
    }

    private fun skipWhiteSpace() {
        while (offset < currentLine.length && whiteSpaces.contains(currentLine[offset])) {
            offset++
        }
    }
}
