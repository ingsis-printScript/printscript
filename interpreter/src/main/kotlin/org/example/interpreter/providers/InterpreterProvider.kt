package org.example.interpreter.providers

import org.example.common.ErrorHandler
import org.example.common.PrintScriptIterator
import org.example.common.results.Result
import org.example.interpreter.Interpreter
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.OutputPrinter

interface InterpreterProvider {
    fun provide(
        iterator: PrintScriptIterator<Result>,
        inputProvider: InputProvider,
        outputPrinter: OutputPrinter,
        errorHandler: ErrorHandler
    ): Interpreter
}
