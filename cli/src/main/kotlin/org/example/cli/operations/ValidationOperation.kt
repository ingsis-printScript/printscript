package org.example.cli.operations

import org.example.cli.util.CliErrorHandler
import org.example.integration.Parser

class ValidationOperation(private val version: String, private val reader: Iterator<String>) : Operation {
    override fun execute() {
        val handler = CliErrorHandler()
        try {
            Parser().validate(reader, version, handler)
        } catch (e: Exception) {
            handler.handleError(e.message ?: e.toString())
        }
    }
}
