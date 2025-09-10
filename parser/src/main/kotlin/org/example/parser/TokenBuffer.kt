package org.example.parser

import org.example.common.PrintScriptIterator
import org.example.token.Token
import org.example.common.enums.TokenType
import org.example.common.exceptions.NoMoreTokensAvailableException

class TokenBuffer(private val tokens: PrintScriptIterator<Token>) : PrintScriptIterator<Token> {
    private val buffer = mutableListOf<Token>()
    private var index = 0

    private fun fillBuffer(n: Int = 1) {
        while (buffer.size < index + n && tokens.hasNext()) {
            buffer.add(tokens.getNext())
        }
    }

    override fun hasNext(): Boolean {
        fillBuffer()
        return index < buffer.size
    }

    override fun getNext(): Token {
        fillBuffer()
        if (index >= buffer.size) throw NoMoreTokensAvailableException()
        return buffer[index++]
    }

    fun peek(): Token {
        fillBuffer()
        if (index >= buffer.size) throw NoMoreTokensAvailableException()
        return buffer[index]
    }

    fun lookahead(n: Int): Token {
        fillBuffer(n)
        if (index + n - 1 >= buffer.size) throw NoMoreTokensAvailableException()
        return buffer[index + n - 1]
    }

    fun commit(consumed: Int) {
        if (consumed > 0) {
            buffer.subList(0, consumed).clear()
            index = 0
        }
    }

    fun isAtEnd(): Boolean {
        fillBuffer()
        return index >= buffer.size && !tokens.hasNext()
    }
}
