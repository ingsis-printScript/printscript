package org.example.parser.parsers.builders.block

import org.example.common.PrintScriptIterator
import org.example.token.Token

class ListIterator(val tokens: List<Token>) : PrintScriptIterator<Token> {
    private val iterator = tokens.iterator()
    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun getNext(): Token {
        return iterator.next()
    }
}
