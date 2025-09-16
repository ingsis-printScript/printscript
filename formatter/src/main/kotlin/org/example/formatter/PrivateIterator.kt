package org.example.formatter

import org.example.common.PrintScriptIterator
import org.example.common.results.Result

class PrivateIterator(val iterator: PrintScriptIterator<Result>) {
    fun hasNext(): Boolean = iterator.hasNext()
}
