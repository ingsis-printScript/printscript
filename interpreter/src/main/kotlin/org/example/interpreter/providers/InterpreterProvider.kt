package org.example.interpreter.providers

import org.example.ast.ASTNode
import org.example.common.PrintScriptIterator
import org.example.interpreter.Interpreter
import org.example.interpreter.input.InputProvider
import org.example.interpreter.output.ErrorHandler
import org.example.interpreter.output.OutputPrinter

interface InterpreterProvider {
    fun provide(
        iterator: PrintScriptIterator<ASTNode>,
        inputProvider: InputProvider,
        outputPrinter: OutputPrinter,
        errorHandler: ErrorHandler
    ): Interpreter
}
