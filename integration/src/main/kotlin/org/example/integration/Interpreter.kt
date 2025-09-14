package org.example.integration

import org.example.interpreter.org.example.interpreter.input.InputProvider
import org.example.common.ErrorHandler
import org.example.interpreter.org.example.interpreter.output.OutputPrinter
import org.example.interpreter.org.example.interpreter.providers.InterpreterVersionProvider
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider

class Interpreter {
    fun execute(
        src: Iterator<String>, // TODO for adapter convert from input stream
        version: String,
        emitter: OutputPrinter,
        handler: ErrorHandler, // keep throws??
        provider: InputProvider
    ) : Unit {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))
        val interpreter = InterpreterVersionProvider().with(version).provide() // TODO: pass parser, executer, validator...
        while (interpreter.hasNext()) {
            interpreter.getNext() // todo: duda con que sea un Iterator<Result> teniendo emitter y handler
        }

    }
}