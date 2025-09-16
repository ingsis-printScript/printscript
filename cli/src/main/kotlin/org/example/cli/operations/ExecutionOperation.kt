package org.example.cli.operations

import org.example.cli.util.CliErrorHandler
import org.example.cli.util.CliPrinter
import org.example.integration.Interpreter
import org.example.interpreter.input.ConsoleInputProvider


class ExecutionOperation(private val version: String, private val reader: Iterator<String>) : Operation {
    override fun execute() {
        val handler = CliErrorHandler()
        val printer = CliPrinter()
        val inputProvider = ConsoleInputProvider()
        try {
            Interpreter().execute(reader, version, printer, handler, inputProvider)
        } catch (e: Exception) {
            handler.handleError(e.message ?: e.toString())
        }
    }
}
