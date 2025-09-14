package org.example.integration

import org.example.interpreter.org.example.interpreter.input.InputProvider
import org.example.interpreter.org.example.interpreter.output.ErrorHandler
import org.example.interpreter.org.example.interpreter.output.OutputPrinter
import org.example.lexer.provider.LexerVersionProvider
import org.example.parser.TokenBuffer
import org.example.parser.provider.ParserVersionProvider

class Interpreter {
    fun execute(
        src: Iterator<String>,
        version: String,
        emitter: OutputPrinter,
        handler: ErrorHandler,
        provider: InputProvider
    ) {
        val lexer = LexerVersionProvider().with(version).provide(src)
        val parser = ParserVersionProvider().with(version).provide(TokenBuffer(lexer))

    }
}