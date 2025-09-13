package org.example.parser

import org.example.common.PrintScriptIterator
import org.example.token.Token
import java.util.Queue

class MockPSIterator(private val tokens: Queue<Token>) : PrintScriptIterator<Token> {
    override fun hasNext(): Boolean {
        return tokens.isNotEmpty()
    }

    override fun getNext(): Token {
        return tokens.poll()
    }
}
