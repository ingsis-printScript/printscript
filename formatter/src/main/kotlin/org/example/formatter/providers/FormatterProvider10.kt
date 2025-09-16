package org.example.formatter.providers

import org.example.common.PrintScriptIterator
import org.example.common.results.Result
import org.example.formatter.CompositeASTFormat
import org.example.formatter.Formatter
import org.example.formatter.formatters.BinaryExpressionFormat
import org.example.formatter.formatters.NumberExpressionFormat
import org.example.formatter.formatters.PrintFunctionFormat
import org.example.formatter.formatters.StringExpressionFormat
import org.example.formatter.formatters.SymbolExpressionFormat
import org.example.formatter.formatters.VariableAssignerFormat
import org.example.formatter.formatters.VariableDeclaratorFormat
import java.io.InputStream
import java.io.Writer

class FormatterProvider10() : FormatterProvider {

    override fun provide(nodes: PrintScriptIterator<Result>, writer: Writer, inputStream: InputStream): Formatter {
        val formats = listOf(
            BinaryExpressionFormat(),
            NumberExpressionFormat(),
            StringExpressionFormat(),
            VariableDeclaratorFormat(),
            PrintFunctionFormat(),
            SymbolExpressionFormat(),
            VariableAssignerFormat()
        )
        return Formatter(nodes, writer, CompositeASTFormat(formats), inputStream)
    }
}
