package org.example.parser

import org.example.common.PrintScriptIterator
import org.example.common.exceptions.NoMoreTokensAvailableException
import org.example.token.Token

class TokenBuffer(private val tokens: PrintScriptIterator<Token>) : PrintScriptIterator<Token> {
    private val buffer = mutableListOf<Token>()
    private var index = 0
    private var baseOffset = 0

    private fun fillBuffer(n: Int = 1) {
        while (buffer.size < index + n && tokens.hasNext()) {
            buffer.add(tokens.getNext())
        }
    }

    override fun hasNext(): Boolean { fillBuffer(); return index < buffer.size }
    override fun getNext(): Token {
        fillBuffer()
        if (index >= buffer.size) throw NoMoreTokensAvailableException()
        return buffer[index++]
    }
    fun lookahead(n: Int): Token {
        fillBuffer(n)
        if (index + n - 1 >= buffer.size) throw NoMoreTokensAvailableException()
        return buffer[index + n - 1]
    }

    fun commit(consumed: Int) {
        require(consumed >= 0) { "negative 'consumed'" }
        val k = minOf(consumed, buffer.size)
        if (k > 0) {
            baseOffset += k
            buffer.subList(0, k).clear()
            index = maxOf(0, index - k)
        }
    }

    fun isAtEnd(from: Int = index): Boolean {
        fillBuffer()
        return from >= buffer.size && !tokens.hasNext()
    }
}

