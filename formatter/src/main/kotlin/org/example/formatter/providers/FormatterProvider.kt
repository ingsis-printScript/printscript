package org.example.formatter.providers

import org.example.common.PrintScriptIterator
import org.example.common.results.Result
import org.example.formatter.Formatter
import java.io.InputStream
import java.io.Writer

interface FormatterProvider {
    fun provide(nodes: PrintScriptIterator<Result>, writer: Writer, inputStream: InputStream): Formatter
}
