package org.example.cli.operations

import org.example.cli.util.CliErrorHandler
import org.example.integration.Formatter
import java.io.InputStream
import java.io.Writer

class FormattingOperation(
    private val version: String,
    private val reader: Iterator<String>,
    private val writer: Writer,
    private val config: InputStream
) : Operation {
    override fun execute() {
        val handler = CliErrorHandler()
        try {
            Formatter().format(reader, version, config, writer)
        } catch (e: Exception) {
            handler.handleError(e.message ?: e.toString())
        }
    }
}
