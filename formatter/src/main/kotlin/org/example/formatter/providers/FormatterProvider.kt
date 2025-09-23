package org.example.formatter.providers

import org.example.common.PrintScriptIterator
import org.example.formatter.Formatter
import org.example.token.Token
import java.io.InputStream
import java.io.Writer

interface FormatterProvider {
    fun provide(nodes: PrintScriptIterator<Token>, writer: Writer, inputStream: InputStream): Formatter
}
