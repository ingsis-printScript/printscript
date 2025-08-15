package org.example.lexer

interface PrintScriptIterator<T> {
    fun hasNext():Boolean
    fun getNext():T
}