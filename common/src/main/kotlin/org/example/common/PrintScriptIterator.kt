package org.example.common

interface PrintScriptIterator<T> {
    fun hasNext(): Boolean
    fun getNext(): T
}
