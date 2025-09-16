package org.example.cli.operations

import org.example.cli.util.CliErrorHandler
import org.example.integration.Linter
import java.io.InputStream

class AnalyzingOperation(
    private val version: String,
    private val reader: Iterator<String>,
    private val config: InputStream
) : Operation {
    override fun execute() {
        val handler = CliErrorHandler()
        try {
            Linter().lint(reader, version, config, handler)
        } catch (e: Exception) {
            handler.handleError(e.message ?: e.toString())
        }
    }
}
