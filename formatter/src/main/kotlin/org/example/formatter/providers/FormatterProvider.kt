package org.example.formatter.providers


import org.example.common.PrintScriptIterator
import org.example.common.results.Result
import org.example.formatter.Formatter
import org.example.formatter.Ruler
import java.io.Writer

interface FormatterProvider {
    fun provide(nodes: PrintScriptIterator<Result>, writer: Writer): Formatter
}
